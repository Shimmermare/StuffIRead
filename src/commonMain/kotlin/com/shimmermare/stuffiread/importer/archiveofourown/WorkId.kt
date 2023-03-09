package com.shimmermare.stuffiread.importer.archiveofourown

@JvmInline
value class WorkId(val value: UInt) {
    override fun toString(): String {
        return value.toString()
    }
}
