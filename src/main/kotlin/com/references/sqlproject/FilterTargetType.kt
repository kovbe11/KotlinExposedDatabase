package com.references.sqlproject

enum class FilterTargetType {
    Items {
        override fun isEnabled(filterTarget: FilterTargetType): Boolean {
            return filterTarget == Items
        }
    }, Shops {
        override fun isEnabled(filterTarget: FilterTargetType): Boolean {
            return filterTarget == Shops
        }
    }, Orders {
        override fun isEnabled(filterTarget: FilterTargetType): Boolean {
            return filterTarget == Orders
        }
    }, Sales {
        override fun isEnabled(filterTarget: FilterTargetType): Boolean {
            return filterTarget == Sales
        }
    }, All {
        override fun isEnabled(filterTarget: FilterTargetType): Boolean {
            return true
        }
    };

    abstract fun isEnabled(filterTarget: FilterTargetType): Boolean
}