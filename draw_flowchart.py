"""生成校园宿舍报修维修管理系统业务流程图"""
from PIL import Image, ImageDraw, ImageFont
import os, sys

# 尝试加载中文字体
font_paths = [
    'C:/Windows/Fonts/msyh.ttc',       # 微软雅黑
    'C:/Windows/Fonts/simhei.ttf',      # 黑体
    'C:/Windows/Fonts/simsun.ttc',      # 宋体
    '/usr/share/fonts/truetype/wqy/wqy-zenhei.ttc',
    '/System/Library/Fonts/PingFang.ttc',
]
font = None
for fp in font_paths:
    if os.path.exists(fp):
        try:
            font = ImageFont.truetype(fp, 16)
            font_small = ImageFont.truetype(fp, 13)
            font_title = ImageFont.truetype(fp, 20)
            break
        except:
            continue
if not font:
    font = ImageFont.load_default()
    font_small = font
    font_title = font

W, H = 900, 650
img = Image.new('RGB', (W, H), 'white')
draw = ImageDraw.Draw(img)

# 颜色
C_STU = (230, 242, 255)    # 学生 - 浅蓝
C_ADMIN = (230, 255, 230)  # 管理员 - 浅绿
C_SYS = (255, 243, 224)    # 系统 - 浅橙
C_BORDER = (80, 80, 80)
C_TEXT = (30, 30, 30)
C_ARROW = (120, 120, 120)
C_BG = (245, 245, 250)

def rounded_box(x1, y1, x2, y2, fill, border=C_BORDER, r=8):
    draw.rounded_rectangle([x1, y1, x2, y2], radius=r, fill=fill, outline=border, width=2)

def arrow(x1, y1, x2, y2, label='', color=C_ARROW):
    draw.line([(x1, y1), (x2, y2)], fill=color, width=2)
    # 箭头
    mx, my = (x1 + x2) / 2, (y1 + y2) / 2
    if abs(x2 - x1) > abs(y2 - y1):
        direction = 1 if x2 > x1 else -1
        draw.polygon([(x2, y2), (x2 - 10 * direction, y2 - 5), (x2 - 10 * direction, y2 + 5)], fill=color)
    else:
        direction = 1 if y2 > y1 else -1
        draw.polygon([(x2, y2), (x2 - 5, y2 - 10 * direction), (x2 + 5, y2 - 10 * direction)], fill=color)

def draw_box(x, y, w, h, text, fill, sub=''):
    rounded_box(x, y, x + w, y + h, fill)
    # 主文字居中
    bbox = draw.textbbox((0, 0), text, font=font)
    tw = bbox[2] - bbox[0]
    th = bbox[3] - bbox[1]
    draw.text((x + (w - tw) / 2, y + (h - th) / 2 - 6), text, fill=C_TEXT, font=font)
    if sub:
        bbox2 = draw.textbbox((0, 0), sub, font=font_small)
        sw = bbox2[2] - bbox2[0]
        draw.text((x + (w - sw) / 2, y + h - 22), sub, fill=(150, 100, 100), font=font_small)

# 标题
draw.text((W//2 - 200, 8), '校园宿舍报修维修管理系统 - 业务流程图', fill=(50, 50, 50), font=font_title)

# 列坐标
col1, col2, col3 = 50, 350, 620  # 学生列, 系统列, 管理列
box_w, box_h = 200, 50
gap_y = 85
start_y = 55

# === 学生流程（左列）===
draw_box(col1, start_y, box_w, box_h, '注册账号', C_STU)
draw.text((col1 + 5, start_y + 5), '学号+密码', fill=(100,120,140), font=font_small)

arrow(col1 + box_w//2, start_y + box_h, col1 + box_w//2, start_y + gap_y, '注册成功')

draw_box(col1, start_y + gap_y, box_w, box_h, '登录系统', C_STU)
arrow(col1 + box_w//2, start_y + gap_y + box_h, col1 + box_w//2, start_y + gap_y * 2, '登录成功')

# 提交报修 - 中间用系统列
draw_box(col1, start_y + gap_y * 2, box_w, box_h, '提交报修', C_STU)
draw.text((col1 + 5, start_y + gap_y * 2 + 5), '选择宿舍+故障类型', fill=(100,120,140), font=font_small)

# 系统处理：报修单创建
arrow(col1 + box_w, start_y + gap_y * 2 + box_h//2, col2, start_y + gap_y * 2 + box_h//2)
draw_box(col2, start_y + gap_y * 2, box_w, box_h, '创建报修单', C_SYS)
draw.text((col2 + 5, start_y + gap_y * 2 + 5), '状态=待处理(0)', fill=(140, 110, 60), font=font_small)

arrow(col2 + box_w//2, start_y + gap_y * 2 + box_h, col2 + box_w//2, start_y + gap_y * 3, '', C_ARROW)

# 接单（管理员）
draw_box(col3, start_y + gap_y * 3, box_w, box_h, '接单', C_ADMIN)
draw.text((col3 + 5, start_y + gap_y * 3 + 5), '记录adminId', fill=(80, 130, 80), font=font_small)

arrow(col2 + box_w//2, start_y + gap_y * 3, col3, start_y + gap_y * 3 + box_h//2, '接单')
arrow(col3 + box_w//2, start_y + gap_y * 3 + box_h, col3 + box_w//2, start_y + gap_y * 4)

# 维修中系统态
draw_box(col2, start_y + gap_y * 4, box_w, box_h, '状态更新', C_SYS)
draw.text((col2 + 5, start_y + gap_y * 4 + 5), '状态=维修中(1)', fill=(140, 110, 60), font=font_small)

arrow(col2 + box_w//2, start_y + gap_y * 4 + box_h, col2 + box_w//2, start_y + gap_y * 5)

# 完成维修
draw_box(col3, start_y + gap_y * 5, box_w, box_h, '完成维修', C_ADMIN)
draw.text((col3 + 5, start_y + gap_y * 5 + 5), '记录finishTime', fill=(80, 130, 80), font=font_small)

arrow(col2 + box_w//2, start_y + gap_y * 5, col3, start_y + gap_y * 5 + box_h//2, '完成')
arrow(col3 + box_w//2, start_y + gap_y * 5 + box_h, col3 + box_w//2, start_y + gap_y * 6)

# 已完成系统态
draw_box(col2, start_y + gap_y * 6, box_w, box_h, '更新状态', C_SYS)
draw.text((col2 + 5, start_y + gap_y * 6 + 5), '状态=已完成(2)', fill=(140, 110, 60), font=font_small)

# 学生查看+评价
arrow(col2 + box_w//2, start_y + gap_y * 6 + box_h, col2 + box_w//2, start_y + gap_y * 7)
arrow(col1 + box_w//2, start_y + gap_y * 6 + box_h//2, col2, start_y + gap_y * 6 + box_h//2, 'WebSocket推送')

draw_box(col1, start_y + gap_y * 7, box_w, box_h, '查看进度+评价', C_STU)
draw.text((col1 + 5, start_y + gap_y * 7 + 5), '5 星评分+文字', fill=(100,120,140), font=font_small)

# 状态机说明
sy = start_y + gap_y * 8 + 20
draw.rounded_rectangle([80, sy, 820, sy + 65], radius=6, fill=(240, 248, 255), outline=(100, 149, 237), width=1)
draw.text((100, sy + 8), '状态机流转：', fill=(50, 50, 50), font=font)
draw.text((100, sy + 33), '待处理 (0)  ──[接单]──▶  维修中 (1)  ──[完成]──▶  已完成 (2)', fill=(80, 80, 200), font=font_small)

# 角色标注
for col, label, color in [(col1, '学生端', C_STU), (col2, '系统端', C_SYS), (col3, '管理员端', C_ADMIN)]:
    x = col + box_w//2 - 30
    draw.rounded_rectangle([x, 42, x + 60, 56], radius=4, fill=color, outline=C_BORDER, width=1)
    bbox = draw.textbbox((0, 0), label, font=font_small)
    draw.text((x + (60 - (bbox[2]-bbox[0]))//2, 43), label, fill=C_TEXT, font=font_small)

out_path = os.path.join(os.path.dirname(__file__), '流程图_业务流程图.png')
img.save(out_path)
print(f'Saved: {out_path}')
print(f'Size: {W}x{H}')
