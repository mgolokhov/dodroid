package doit.study.droid.data.local

import doit.study.droid.data.local.entity.Question
import doit.study.droid.data.local.entity.Tag

interface DataSourceDb {
    fun getQuestions(): List<Question>
    fun getQuestion(id: Int): Question

    fun saveQuestion(question: Question)
    fun saveQuestions(vararg question: Question)

    fun getTags(): List<Tag>
    fun getTag(id: Int): Tag

    fun saveTag(tag: Tag)
    fun saveTags(vararg tag: Tag)
}
