/**
 * Copyright (C) 2016 Newland Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zakl.nettyrpc.common.parallel.policy;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:RejectedPolicyType.java
 * @description:RejectedPolicyType功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
public enum RejectedPolicyType {
    ABORT_POLICY("AbortPolicy"),
    BLOCKING_POLICY("BlockingPolicy"),
    CALLER_RUNS_POLICY("CallerRunsPolicy"),
    DISCARDED_POLICY("DiscardedPolicy"),
    REJECTED_POLICY("RejectedPolicy");

    private String value;

    private RejectedPolicyType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RejectedPolicyType fromString(String value) {
        for (RejectedPolicyType type : RejectedPolicyType.values()) {
            if (type.getValue().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }

        throw new IllegalArgumentException("Mismatched type with value=" + value);
    }

    @Override
    public String toString() {
        return value;
    }
}

