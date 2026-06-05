package com.mycity.dormitory.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 状态机单元测试 — 验证报修单状态的流转规则
 *
 * 测试覆盖：
 *   ✅ 正常流转：待处理 → 维修中 → 已完成
 *   ❌ 非法回退：维修中 → 待处理、已完成 → 维修中
 *   ❌ 非法跳步：待处理 → 已完成
 *   ❌ 重复提交：同一状态提交
 *   ❌ 终态不可变：已完成不可再变
 *   ✅ fromCode 查找正确
 *   ❌ 无效 code 抛异常
 */
@DisplayName("RepairStatus 状态机测试")
class RepairStatusTest {

    @Nested
    @DisplayName("fromCode() — 根据数字码查找枚举")
    class FromCodeTest {

        @Test
        @DisplayName("0 → PENDING(待处理)")
        void fromCode_0_shouldReturnPending() {
            assertEquals(RepairStatus.PENDING, RepairStatus.fromCode(0));
        }

        @Test
        @DisplayName("1 → IN_PROGRESS(维修中)")
        void fromCode_1_shouldReturnInProgress() {
            assertEquals(RepairStatus.IN_PROGRESS, RepairStatus.fromCode(1));
        }

        @Test
        @DisplayName("2 → COMPLETED(已完成)")
        void fromCode_2_shouldReturnCompleted() {
            assertEquals(RepairStatus.COMPLETED, RepairStatus.fromCode(2));
        }

        @Test
        @DisplayName("无效 code 99 → 抛出 IllegalArgumentException")
        void fromCode_invalidCode_shouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> RepairStatus.fromCode(99),
                "无效的状态码: 99");
        }

        @Test
        @DisplayName("null → 抛出 IllegalArgumentException")
        void fromCode_null_shouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> RepairStatus.fromCode(null),
                "状态码不能为空");
        }
    }

    @Nested
    @DisplayName("canTransitionTo() — 状态机流转校验")
    class CanTransitionToTest {

        // ── 正常流转 ──

        @Test
        @DisplayName("✅ 待处理 → 维修中（接单）")
        void pending_to_inProgress_shouldAllowed() {
            assertTrue(RepairStatus.PENDING.canTransitionTo(RepairStatus.IN_PROGRESS));
        }

        @Test
        @DisplayName("✅ 维修中 → 已完成（维修完成）")
        void inProgress_to_completed_shouldAllowed() {
            assertTrue(RepairStatus.IN_PROGRESS.canTransitionTo(RepairStatus.COMPLETED));
        }

        // ── 回退（回退一律不允许） ──

        @Test
        @DisplayName("❌ 维修中 → 待处理（回退）")
        void inProgress_to_pending_shouldDeny() {
            assertFalse(RepairStatus.IN_PROGRESS.canTransitionTo(RepairStatus.PENDING));
        }

        @Test
        @DisplayName("❌ 已完成 → 维修中（回退）")
        void completed_to_inProgress_shouldDeny() {
            assertFalse(RepairStatus.COMPLETED.canTransitionTo(RepairStatus.IN_PROGRESS));
        }

        @Test
        @DisplayName("❌ 已完成 → 待处理（回退）")
        void completed_to_pending_shouldDeny() {
            assertFalse(RepairStatus.COMPLETED.canTransitionTo(RepairStatus.PENDING));
        }

        // ── 跳步 ──

        @Test
        @DisplayName("❌ 待处理 → 已完成（跳步，缺少维修中）")
        void pending_to_completed_shouldDeny() {
            assertFalse(RepairStatus.PENDING.canTransitionTo(RepairStatus.COMPLETED));
        }

        // ── 重复提交 ──

        @Test
        @DisplayName("❌ 待处理 → 待处理（重复提交）")
        void pending_to_pending_shouldDeny() {
            assertFalse(RepairStatus.PENDING.canTransitionTo(RepairStatus.PENDING));
        }

        @Test
        @DisplayName("❌ 维修中 → 维修中（重复提交）")
        void inProgress_to_inProgress_shouldDeny() {
            assertFalse(RepairStatus.IN_PROGRESS.canTransitionTo(RepairStatus.IN_PROGRESS));
        }

        @Test
        @DisplayName("❌ 已完成 → 已完成（重复提交）")
        void completed_to_completed_shouldDeny() {
            assertFalse(RepairStatus.COMPLETED.canTransitionTo(RepairStatus.COMPLETED));
        }

        // ── 终态不可变 ──

        @Test
        @DisplayName("❌ 已完成：终态不可再变")
        void completed_to_any_shouldDeny() {
            assertFalse(RepairStatus.COMPLETED.canTransitionTo(RepairStatus.PENDING));
            assertFalse(RepairStatus.COMPLETED.canTransitionTo(RepairStatus.IN_PROGRESS));
            assertFalse(RepairStatus.COMPLETED.canTransitionTo(RepairStatus.COMPLETED));
        }

        // ── null 参数 ──

        @Test
        @DisplayName("❌ 目标状态为 null")
        void any_to_null_shouldDeny() {
            assertFalse(RepairStatus.PENDING.canTransitionTo(null));
            assertFalse(RepairStatus.IN_PROGRESS.canTransitionTo(null));
            assertFalse(RepairStatus.COMPLETED.canTransitionTo(null));
        }
    }
}
