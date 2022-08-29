package com.megapixel.trashbench.Model

class Targets
{
    private var targetid: String = ""
    private var publisher: String = ""
    private var target: String = ""
    private var time: String = ""


    constructor()
    constructor(targetid: String, publisher: String, target: String, time: String) {
        this.targetid = targetid
        this.publisher = publisher
        this.target = target
        this.time = time
    }


    fun getTargetid(): String
    {
        return targetid
    }

    fun setTargetid(targetid: String)
    {
        this.targetid = targetid
    }

    fun getPublisher(): String
    {
        return publisher
    }

    fun setPublisher(publisher: String)
    {
        this.publisher = publisher
    }

    fun getTarget(): String
    {
        return target
    }

    fun setTarget(target: String)
    {
        this.target = target
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