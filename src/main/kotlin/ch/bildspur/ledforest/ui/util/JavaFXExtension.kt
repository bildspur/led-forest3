package ch.bildspur.ledforest.ui.util

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView

fun TreeView<TagItem>.items(current: TreeItem<TagItem> = this.root,
                            items: MutableList<TreeItem<TagItem>> = mutableListOf<TreeItem<TagItem>>())
        : MutableList<TreeItem<TagItem>> {
    items.add(current)

    current.children.forEach {
        this.items(it, items)
    }

    return items
}