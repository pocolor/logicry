package cz.pocolor.game.logicry.utils

import kotlinx.serialization.Serializable
import java.util.Spliterator
import java.util.function.BiConsumer
import java.util.function.Consumer

/**
 * Map made for a small amount of elements.
 * Its purpose is to update and keep track of values.
 * Keys cannot be removed nor updated.
 */
@Serializable
class ImmutableKeysMap<K: Comparable<K>, V> : Map<K, V>, Iterable<MutableMap.MutableEntry<K, V>>, java.io.Serializable {
    @Serializable
    private class SimpleEntry<K, V>(override val key: K, override var value: V) : MutableMap.MutableEntry<K, V>, java.io.Serializable {
        override fun setValue(newValue: V): V {
            value = newValue
            return newValue
        }

        override fun equals(other: Any?): Boolean = other is MutableMap.MutableEntry<*, *> && key == other.key && value == other.value

        override fun hashCode(): Int = 31 * key.hashCode() + value.hashCode()

        override fun toString(): String = "SimpleEntry(key=$key, value=$value)"
    }

    private val listEntries: List<MutableMap.MutableEntry<K, V>>

    constructor(keys: Collection<K>, defaultValue: V) {
        listEntries = keys.map { SimpleEntry(it, defaultValue) }.sortedBy { it.key }
        this.keys = keys.toSet()
        this.size = listEntries.size

        validateNonDuplicateKeys()
    }

    constructor(vararg keys: K, defaultValue: V) : this(listOf(*keys), defaultValue)

    constructor(vararg pairs: Pair<K, V>) {
        this.listEntries = pairs.map { SimpleEntry(it.first, it.second) }.sortedBy { it.key }
        this.keys = pairs.map { it.first }.toSet()
        this.size = listEntries.size

        validateNonDuplicateKeys()
    }

    fun validateNonDuplicateKeys() {
        require(listEntries.size == keys.size) {
            "Map cannot contain duplicate keys."
        }
    }

    private fun binarySearch(key: K): MutableMap.MutableEntry<K, V>? {
        if (listEntries.isEmpty()) return null

        var left = 0
        var middle = size shr 1
        var right = size - 1

        while (left <= middle) {
            if (listEntries[middle].key == key) return listEntries[middle]

            if (listEntries[middle].key < key) left = middle + 1
            else right = middle - 1

            middle = (left + right) shr 1
        }

        return null
    }

    override val entries: Set<Map.Entry<K, V>>
        get() = listEntries.toSet()
    override val keys: Set<K>
    override val size: Int
    override val values: Collection<V>
        get() = listEntries.map { it.value }

    override fun containsKey(key: K): Boolean = keys.contains(key)

    override fun containsValue(value: V): Boolean = listEntries.any { it.value == value }

    override fun get(key: K): V? = binarySearch(key)?.value

    override fun isEmpty(): Boolean = size == 0

    override fun forEach(action: BiConsumer<in K, in V>) = listEntries.forEach { (key, value) -> action.accept(key, value) }

    override fun getOrDefault(key: K, defaultValue: V): V = get(key) ?: defaultValue

    override fun iterator(): Iterator<MutableMap.MutableEntry<K, V>> = listEntries.iterator()

    override fun forEach(action: Consumer<in MutableMap.MutableEntry<K, V>>?) = listEntries.forEach(action)

    override fun spliterator(): Spliterator<MutableMap.MutableEntry<K, V>> = listEntries.spliterator()

    override fun equals(other: Any?): Boolean = other is ImmutableKeysMap<K, V> && listEntries == other.listEntries

    override fun hashCode(): Int = listEntries.hashCode()

    override fun toString(): String = "ImmutableKeysMap(entries=$entries)"

    /**
     * Sets value of the given key.
     * If the map doesn't contain the given key, [UnsupportedOperationException] is thrown.
     *
     * @param key The key of the value.
     * @param value A value to be assigned to the given key.
     *
     * @throws UnsupportedOperationException If the map doesn't contain the given key.
     */
    operator fun set(key: K, value: V) {
        val entry = binarySearch(key) ?: throw UnsupportedOperationException("Map doesn't contain key: $key. Cannot create new keys.")
        entry.setValue(value)
    }
}