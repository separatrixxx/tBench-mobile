package com.megapixel.trashbench.Model

class Clubs
{
    private var clubname: String = ""
    private var clubid: String = ""
    private var clubimage: String = ""
    private var clubcover: String = ""
    private var clubdescription: String = ""
    private var ownerId: String = ""
    private var idClub: String = ""


    constructor()


    constructor(clubname: String, clubid: String, clubimage: String, clubcover: String, clubdescription: String, ownerId: String, idClub: String) {
        this.clubname = clubname
        this.clubid = clubid
        this.clubimage = clubimage
        this.clubcover = clubcover
        this.clubdescription = clubdescription
        this.ownerId = ownerId
        this.idClub = idClub
    }


    fun getClubname(): String
    {
        return clubname
    }

    fun setClubname(clubname: String)
    {
        this.clubname = clubname
    }

    fun getClubid(): String
    {
        return clubid
    }

    fun setClubid(clubid: String)
    {
        this.clubid = clubid
    }

    fun getClubimage(): String
    {
        return clubimage
    }

    fun setClubimage(clubimage: String)
    {
        this.clubimage = clubimage
    }

    fun getClubcover(): String
    {
        return clubcover
    }

    fun setClubcover(clubcover: String)
    {
        this.clubcover = clubcover
    }

    fun getClubdescription(): String
    {
        return clubdescription
    }

    fun setClubdescription(clubdescription: String)
    {
        this.clubdescription = clubdescription
    }

    fun getOwnerId(): String
    {
        return ownerId
    }

    fun setOwnerId(ownerId: String)
    {
        this.ownerId = ownerId
    }

    fun getIdClub(): String
    {
        return idClub
    }

    fun setIdClub(idClub: String)
    {
        this.idClub = idClub
    }
}