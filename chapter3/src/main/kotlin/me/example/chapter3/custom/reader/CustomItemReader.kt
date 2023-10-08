package me.example.chapter3.custom.reader

import org.springframework.batch.item.ItemReader

/**
 * @author Doyeop Kim
 * @since 2023/10/08
 */
class CustomItemReader<T>(private val items: MutableList<T>) : ItemReader<T> {
    override fun read(): T? {
        if (items.isNotEmpty()) {
            return items.removeAt(0)
        }

        return null
    }
}