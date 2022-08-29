package com.megapixel.trashbench.Model

class Post
{
    private var postid: String = ""
    private var postimage: String = ""
    private var publisher: String = ""
    private var description: String = ""
    private var time: String = ""


    constructor()


    constructor(postid: String, postimage: String, publisher: String, description: String, time: String)
    {
        this.postid = postid
        this.postimage = postimage
        this.publisher = publisher
        this.description = description
        this.time = time
    }


    fun getPostid(): String
    {
        return postid
    }

    fun setPostid(postid: String)
    {
        this.postid = postid
    }

    fun getPostimage(): String
    {
        return postimage
    }

    fun setPostimage(postimage: String)
    {
        this.postimage = postimage
    }

    fun getPublisher(): String
    {
        return publisher
    }

    fun setPublisher(publisher: String)
    {
        this.publisher = publisher
    }

    fun getDescription(): String
    {
        return description
    }

    fun setDescription(description: String)
    {
        this.description = description
    }

    fun getTime(): String
    {
        return time
    }

    fun setTime(time: String)
    {
        this.time = time
    }
}