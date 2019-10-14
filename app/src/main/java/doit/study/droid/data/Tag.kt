package doit.study.droid.data

import android.database.Cursor

data class Tag(val id: Int, val name: String, val selected: Boolean, val questionsCounter: Int, val questionsStudied: Int) {

    object Table {

        const val NAME = "tags"
        const val _ID = "_id"
        const val TEXT = "text"
        const val SELECTED = "selected"
        // fully qualified names
        const val FQ_ID = "$NAME.$_ID"
        const val FQ_TEXT = "$NAME.$TEXT"
        const val FQ_SELECTION = "$NAME.$SELECTED"
        // helper constants
        const val QTY_WHEN_STUDIED = "3"
        const val TOTAL_COUNTER = "tagTotalCounter"
        const val STUDIED_COUNTER = "tagStudiedCounter"
    }


    override fun toString(): String {
        return String.format("%d# Name: %s, selection: %s", hashCode(), name, selected)
    }


    companion object {
        @JvmStatic
        fun newInstance(c: Cursor): Tag {
            return Tag(c.getInt(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex(Tag.Table.TEXT)),
                    c.getInt(c.getColumnIndex(Tag.Table.SELECTED)) == 1,
                    c.getInt(c.getColumnIndex(Tag.Table.TOTAL_COUNTER)),
                    c.getInt(c.getColumnIndex(Table.STUDIED_COUNTER)))
        }
    }
}
