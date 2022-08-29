package com.megapixel.trashbench.Model

class Comment
{
    private var comment: String = ""
    private var publisher: String = ""
    private var time: String = ""


    constructor()


    constructor(comment: String, publisher: String, time: String) {
        this.comment = comment
        this.publisher = publisher
        this.time = time
    }


    fun getComment(): String
    {
        return comment
    }

    fun getPublisher(): String
    {
        return publisher
    }

    fun getTime(): String
    {
        return time
    }

    fun setComment(comment: String)
    {
        this.comment = comment
    }

    fun setPublisher(publisher: String)
    {
        this.publisher = publisher
    }

    fun setTime(time: String)
    {
        this.time = time
    }
}