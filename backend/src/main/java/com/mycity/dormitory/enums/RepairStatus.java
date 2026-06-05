package com.mycity.dormitory.enums;

import lombok.Getter;

/**
 * 报修单状态枚举
 * <p>
 * 状态机流转规则：
 * <pre>
 *    待处理(0) ──→ 维修中(1) ──→ 已完成(2)
 *       ↑              ↑              ↑
 *       └── 不可回退 ──┘── 不可回退 ──┘
 *       已完成(2) 为终态，不可再变更
 * </pre>
 */
@Getter
public enum RepairStatus {
    PENDING(0, "待处理"),
    IN_PROGRESS(1, "维修中"),
    COMPLETED(2, "已完成");

    private final int code;
    private final String desc;

    RepairStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据数字 code 查找对应的枚举值
     *
     * @param code 状态码（0/1/2）
     * @return 对应的枚举值
     * @throws IllegalArgumentException 如果 code 无效
     */
    public static RepairStatus fromCode(Integer code) {
        if (code == null) {
            throw new IllegalArgumentException("状态码不能为空");
        }
        for (RepairStatus s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        throw new IllegalArgumentException("无效的状态码: " + code);
    }

    /**
     * 状态机：判断当前状态是否可以转换到目标状态
     * <p>
     * 规则：
     * <ul>
     *   <li>待处理(0) → 维修中(1) ✅</li>
     *   <li>维修中(1) → 已完成(2) ✅</li>
     *   <li>已完成(2) → 任何状态 ❌（终态不可变）</li>
     *   <li>回退操作 ❌（如 维修中→待处理）</li>
     *   <li>跳步操作 ❌（如 待处理→已完成）</li>
     * </ul>
     *
     * @param target 目标状态
     * @return true 表示允许转换
     */
    public boolean canTransitionTo(RepairStatus target) {
        // 如果目标状态为空，不允许
        if (target == null) {
            return false;
        }
        // 如果新状态和当前状态相同，不允许重复提交
        if (this == target) {
            return false;
        }
        // 状态机核心规则
        return switch (this) {
            case PENDING      -> target == IN_PROGRESS;   // 待处理 → 维修中
            case IN_PROGRESS  -> target == COMPLETED;     // 维修中 → 已完成
            case COMPLETED    -> false;                   // 已完成 → 任何状态都不行（终态）
        };
    }
}
