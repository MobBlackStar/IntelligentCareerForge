#!/usr/bin/env python3
"""
Walk the project tree and write every text source file into one PDF (path + line-numbered body).
Excludes this script, the generated PDF, VCS/build caches, and typical binaries.
"""

from __future__ import annotations

import argparse
import os
import sys
import urllib.request
from datetime import datetime, timezone
from pathlib import Path

try:
    from fpdf import FPDF
except ImportError:
    print("Missing dependency: pip install -r scripts/requirements-export-pdf.txt", file=sys.stderr)
    sys.exit(1)


# Directory names: do not descend (build artifacts, tooling caches).
SKIP_DIR_NAMES = frozenset({
    ".git",
    "target",
    "__pycache__",
    ".cursor",
    "node_modules",
    ".mvn",
    ".export_font_cache",
})

# DejaVu Sans Mono (SIL OFL) - fpdf2 no longer ships TTFs; cache locally on first run.
DEJAVU_MONO_URL = (
    "https://raw.githubusercontent.com/dejavu-fonts/dejavu-fonts/"
    "version_2_37/ttf/DejaVuSansMono.ttf"
)

# File suffixes treated as non-source (omit from PDF; optional one-line stub).
BINARY_SUFFIXES = frozenset({
    ".class", ".jar", ".war", ".ear", ".zip", ".png", ".jpg", ".jpeg", ".gif", ".webp",
    ".ico", ".pdf", ".exe", ".dll", ".so", ".dylib", ".bin", ".woff", ".woff2", ".ttf",
    ".eot", ".mp3", ".mp4", ".avi", ".mov",
})

# Extra plain-text extensions (in addition to "unknown" heuristic).
TEXT_SUFFIXES = frozenset({
    ".java", ".kt", ".kts", ".xml", ".fxml", ".css", ".sql", ".properties", ".md",
    ".json", ".yml", ".yaml", ".gradle", ".gitignore", ".env", ".txt", ".csv", ".http",
    ".iml", ".prefs", ".factorypath", ".project", ".classpath",
})

SCRIPT_BASENAME = "export_project_sources_to_pdf.py"
DEFAULT_OUT_NAME = "PROJET_full_source_export.pdf"


def _project_root(script_path: Path) -> Path:
    return script_path.resolve().parent.parent


def _is_under_skipped_dir(rel: Path) -> bool:
    return any(part in SKIP_DIR_NAMES for part in rel.parts)


def _looks_text(data: bytes) -> bool:
    if not data:
        return True
    if b"\x00" in data[:8192]:
        return False
    # UTF-8 BOM ok
    try:
        data.decode("utf-8")
        return True
    except UnicodeDecodeError:
        return False


def _collect_files(root: Path, script_path: Path, output_path: Path) -> list[Path]:
    files: list[Path] = []
    root = root.resolve()
    script_res = script_path.resolve()
    out_res = output_path.resolve()

    for dirpath, dirnames, filenames in os.walk(root, topdown=True):
        dirnames[:] = [d for d in dirnames if d not in SKIP_DIR_NAMES]
        for name in filenames:
            p = Path(dirpath) / name
            try:
                pr = p.resolve()
            except OSError:
                continue
            if pr == script_res or pr == out_res:
                continue
            if name == SCRIPT_BASENAME:
                continue
            rel = p.relative_to(root)
            if _is_under_skipped_dir(rel):
                continue
            files.append(p)

    files.sort(key=lambda x: str(x).lower())
    return files


def _ensure_unicode_mono_font(script_path: Path) -> str | None:
    """
    Return path to DejaVuSansMono.ttf, downloading into scripts/.export_font_cache once.
    Returns None if download fails (caller uses built-in Courier + Latin-1 fallback).
    """
    cache_dir = script_path.resolve().parent / ".export_font_cache"
    cache_dir.mkdir(parents=True, exist_ok=True)
    ttf = cache_dir / "DejaVuSansMono.ttf"
    if ttf.is_file() and ttf.stat().st_size > 10_000:
        return str(ttf)
    try:
        req = urllib.request.Request(
            DEJAVU_MONO_URL,
            headers={"User-Agent": "PROJET-export-script/1.0"},
        )
        with urllib.request.urlopen(req, timeout=60) as resp:
            data = resp.read()
        if len(data) < 10_000:
            return None
        if data[:4] not in (b"\x00\x01\x00\x00", b"true", b"OTTO"):
            return None
        ttf.write_bytes(data)
        return str(ttf)
    except OSError:
        return None


def _latin1_safe(s: str) -> str:
    return s.encode("latin-1", errors="replace").decode("latin-1")


def _mc(pdf: FPDF, w: float, h: float, txt: str) -> None:
    """multi_cell then reset x; fpdf2 leaves x at the line end, which breaks the next multi_cell."""
    pdf.multi_cell(w, h, txt)
    pdf.set_x(pdf.l_margin)


class ExportPDF(FPDF):
    def __init__(self, mono_family: str, use_unicode_font: bool) -> None:
        super().__init__(orientation="P", unit="mm", format="A4")
        self._mono_family = mono_family
        self._use_unicode_font = use_unicode_font

    def footer(self) -> None:
        self.set_y(-12)
        self.set_font(self._mono_family, size=8)
        self.set_text_color(100, 100, 100)
        foot = f"Page {self.page_no()}"
        if not self._use_unicode_font:
            foot = _latin1_safe(foot)
        self.cell(0, 10, foot, align="C")


def _add_file_section(
    pdf: ExportPDF, rel_path: str, lines: list[str], mono: str, unicode_font: bool
) -> None:
    pdf.set_font(mono, size=9)
    pdf.set_text_color(30, 30, 30)
    rp = rel_path.replace("\\", "/")
    if not unicode_font:
        rp = _latin1_safe(rp)
    _mc(pdf, 0, 4.5, rp)
    pdf.ln(1)
    pdf.set_draw_color(200, 200, 200)
    pdf.line(pdf.l_margin, pdf.get_y(), pdf.w - pdf.r_margin, pdf.get_y())
    pdf.ln(2)

    pdf.set_font(mono, size=7)
    for i, line in enumerate(lines, start=1):
        safe = line.rstrip("\n\r").expandtabs(4)
        safe = safe.replace("\r", "")
        if not unicode_font:
            safe = _latin1_safe(safe)
        numbered = f"{i:6d} | {safe}"
        if pdf.get_y() > pdf.h - pdf.b_margin - 6:
            pdf.add_page()
            pdf.set_font(mono, size=7)
        _mc(pdf, 0, 3.6, numbered)
    pdf.ln(4)


def run(root: Path, output: Path) -> None:
    script_path = Path(__file__).resolve()
    root = root.resolve()
    output = output.resolve()

    if not root.is_dir():
        raise SystemExit(f"Root is not a directory: {root}")

    files = _collect_files(root, script_path, output)
    font_path = _ensure_unicode_mono_font(script_path)
    unicode_font = font_path is not None
    mono = "ExportMono" if unicode_font else "courier"
    pdf = ExportPDF(mono_family=mono, use_unicode_font=unicode_font)
    pdf.set_auto_page_break(auto=True, margin=14)
    if unicode_font:
        pdf.add_font("ExportMono", "", font_path)
    pdf.add_page()

    pdf.set_font(mono, size=14)
    pdf.set_text_color(20, 20, 20)
    title = "Project source export (full tree, text files)"
    if not unicode_font:
        title = _latin1_safe(title)
    _mc(pdf, 0, 8, title)
    pdf.set_font(mono, size=9)
    pdf.set_text_color(60, 60, 60)
    ts = datetime.now(timezone.utc).strftime("%Y-%m-%d %H:%M UTC")
    root_txt = root.as_posix()
    for line in (
        f"Root: {root_txt}",
        f"Generated: {ts}",
        f"Files scanned: {len(files)}",
        "Excluded from this PDF: this script, the output PDF, .git, target, __pycache__, "
        ".cursor, node_modules, .mvn, .export_font_cache, and binary extensions.",
        "Font: DejaVu Sans Mono (cached under scripts/.export_font_cache) if available; "
        "otherwise Courier with non-Latin-1 characters replaced.",
    ):
        if not unicode_font:
            line = _latin1_safe(line)
        _mc(pdf, 0, 5, line)
    pdf.ln(6)

    # Table of contents (paths only)
    pdf.set_font(mono, size=10)
    pdf.set_text_color(0, 80, 120)
    idx_title = "Index (relative paths)"
    if not unicode_font:
        idx_title = _latin1_safe(idx_title)
    _mc(pdf, 0, 5, idx_title)
    pdf.ln(1)
    pdf.set_font(mono, size=7)
    pdf.set_text_color(40, 40, 40)
    for p in files:
        rel = p.relative_to(root).as_posix()
        if pdf.get_y() > pdf.h - pdf.b_margin - 8:
            pdf.add_page()
            pdf.set_font(mono, size=7)
        rl = rel if unicode_font else _latin1_safe(rel)
        _mc(pdf, 0, 3.5, rl)
    pdf.add_page()

    for p in files:
        rel = p.relative_to(root).as_posix()
        suf = p.suffix.lower()
        if suf in BINARY_SUFFIXES:
            pdf.set_font(mono, size=9)
            msg = f"[Binary omitted: {rel}]"
            if not unicode_font:
                msg = _latin1_safe(msg)
            _mc(pdf, 0, 5, msg)
            pdf.ln(3)
            if pdf.get_y() > pdf.h - pdf.b_margin - 20:
                pdf.add_page()
            continue

        try:
            raw = p.read_bytes()
        except OSError as e:
            pdf.set_font(mono, size=9)
            msg = f"[Unreadable: {rel} ({e})]"
            if not unicode_font:
                msg = _latin1_safe(msg)
            _mc(pdf, 0, 5, msg)
            pdf.ln(3)
            continue

        is_text = suf in TEXT_SUFFIXES or (suf not in BINARY_SUFFIXES and _looks_text(raw))
        if not is_text:
            pdf.set_font(mono, size=9)
            msg = f"[Non-text skipped: {rel}]"
            if not unicode_font:
                msg = _latin1_safe(msg)
            _mc(pdf, 0, 5, msg)
            pdf.ln(3)
            continue

        text = raw.decode("utf-8")
        lines = text.splitlines()
        if not lines:
            lines = [""]

        if pdf.get_y() > pdf.h - pdf.t_margin - 30:
            pdf.add_page()
        pdf.set_font(mono, size=11)
        pdf.set_text_color(0, 90, 130)
        hdr = f"=== {rel} ==="
        if not unicode_font:
            hdr = _latin1_safe(hdr)
        _mc(pdf, 0, 6, hdr)
        pdf.ln(1)
        _add_file_section(pdf, rel, lines, mono, unicode_font)

    output.parent.mkdir(parents=True, exist_ok=True)
    pdf.output(str(output))
    print(f"Wrote {output} ({len(files)} files indexed, text embedded where applicable).")


def main() -> None:
    default_root = _project_root(Path(__file__))
    parser = argparse.ArgumentParser(description="Export project sources to a single PDF.")
    parser.add_argument("--root", type=Path, default=default_root, help="Project root directory")
    parser.add_argument(
        "--output",
        type=Path,
        default=None,
        help=f"Output PDF path (default: <root>/{DEFAULT_OUT_NAME})",
    )
    args = parser.parse_args()
    out = args.output if args.output else (args.root / DEFAULT_OUT_NAME)
    run(args.root, out)


if __name__ == "__main__":
    main()
