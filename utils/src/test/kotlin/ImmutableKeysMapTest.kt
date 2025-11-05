package cz.pocolor.game.logicry.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ImmutableKeysMapTest {
    @Test
    fun `map shouldn't contain duplicate keys`() {
        assertThrows<IllegalArgumentException> { ImmutableKeysMap("k1", "k1", defaultValue = "value") }
    }

    @Test
    fun `keys should be immutable`() {
        val map = ImmutableKeysMap("k1", "k2", "k3", defaultValue = "value")

        assertThrows<UnsupportedOperationException> { map["newKey"] = "value" }
    }

    @Test
    fun `values should be mutable`() {
        val map = ImmutableKeysMap("k1", "k2", "k3", defaultValue = "value")

        map["k1"] = "newValue"

        assertEquals("newValue", map["k1"])
    }
}