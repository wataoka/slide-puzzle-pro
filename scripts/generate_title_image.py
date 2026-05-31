"""
Generates a 1080x2400 title/promo image for the Play Store screenshot slot.
Design: blue background, app icon, app name, tagline.
"""

from PIL import Image, ImageDraw, ImageFont
import os

SCRIPT_DIR = os.path.dirname(__file__)
PROJECT_DIR = os.path.join(SCRIPT_DIR, "..")
STORE_DIR   = os.path.join(PROJECT_DIR, "store_assets")

WIDTH, HEIGHT = 1080, 2400

BG_COLOR      = (21, 101, 192)   # #1565C0
TILE_WHITE    = (255, 255, 255)
TILE_SHADOW   = (13,  71, 161)
TILE_EMPTY    = (13,  71, 161)
TEXT_WHITE    = (255, 255, 255)
TEXT_MUTED    = (187, 222, 251)  # light blue


def draw_puzzle_icon(draw, cx, cy, size):
    """Draw the 2x2 puzzle icon centered at (cx, cy)."""
    grid     = 2
    padding  = size * 0.10
    gap      = size * 0.06
    tile     = (size - 2 * padding - (grid - 1) * gap) / grid
    radius   = max(4, tile * 0.18)
    offset   = max(2, size * 0.008)

    for row in range(grid):
        for col in range(grid):
            is_empty = (row == grid - 1 and col == grid - 1)
            x0 = cx - size / 2 + padding + col * (tile + gap)
            y0 = cy - size / 2 + padding + row * (tile + gap)
            x1 = x0 + tile
            y1 = y0 + tile

            if is_empty:
                draw.rounded_rectangle([x0, y0, x1, y1], radius=radius, fill=TILE_EMPTY)
            else:
                draw.rounded_rectangle(
                    [x0 + offset, y0 + offset, x1 + offset, y1 + offset],
                    radius=radius, fill=TILE_SHADOW
                )
                draw.rounded_rectangle([x0, y0, x1, y1], radius=radius, fill=TILE_WHITE)


def load_font(size):
    for path in [
        "/System/Library/Fonts/Helvetica.ttc",
        "/System/Library/Fonts/SFNSDisplay.ttf",
        "/System/Library/Fonts/SFNS.ttf",
    ]:
        try:
            return ImageFont.truetype(path, size)
        except Exception:
            continue
    return ImageFont.load_default()


def centered_text(draw, text, font, y, color):
    bbox = draw.textbbox((0, 0), text, font=font)
    w = bbox[2] - bbox[0]
    x = (WIDTH - w) / 2 - bbox[0]
    draw.text((x, y), text, font=font, fill=color)


def main():
    img  = Image.new("RGB", (WIDTH, HEIGHT), BG_COLOR)
    draw = ImageDraw.Draw(img)

    # ── Icon ──────────────────────────────────────────────────────────────
    icon_size = 420
    icon_cx   = WIDTH // 2
    icon_cy   = HEIGHT // 2 - 180
    draw_puzzle_icon(draw, icon_cx, icon_cy, icon_size)

    # ── App name ──────────────────────────────────────────────────────────
    title_font = load_font(108)
    title_y    = icon_cy + icon_size // 2 + 80
    centered_text(draw, "Slide Puzzle", title_font, title_y, TEXT_WHITE)

    # ── "Pro" accent ──────────────────────────────────────────────────────
    pro_font = load_font(68)
    pro_y    = title_y + 130
    centered_text(draw, "P R O", pro_font, pro_y, TEXT_MUTED)

    # ── Save ──────────────────────────────────────────────────────────────
    os.makedirs(STORE_DIR, exist_ok=True)
    out = os.path.join(STORE_DIR, "screenshot_title.png")
    img.save(out, "PNG")
    print(f"Saved → {out}")


if __name__ == "__main__":
    main()
