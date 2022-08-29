package com.megapixel.trashbench.Model

class User
{
    private var username: String = ""
    private var fullname: String = ""
    private var bio: String = ""
    private var city: String = ""
    private var birthday: String = ""
    private var specialty: String = ""
    private var image: String = ""
    private var uid: String = ""
    private var status: String = ""
    private var mp: String = ""
    private var emoji: String = ""
    private var deleted: String = ""


    constructor()


    constructor(username: String, fullname: String, bio: String, city: String, birthday: String, specialty: String, image: String, uid: String, status: String, mp: String, emoji: String, deleted: String)
    {
        this.username = username
        this.fullname = fullname
        this.bio = bio
        this.city = city
        this.birthday = birthday
        this.specialty = specialty
        this.image = image
        this.uid = uid
        this.status = status
        this.mp = mp
        this.emoji = emoji
        this.deleted = deleted
    }


    fun getUsername(): String
    {
        return username
    }

    fun setUsername(username: String)
    {
        this.username = username
    }

    fun getFullname(): String
    {
        return fullname
    }

    fun setFullname(fullname: String)
    {
        this.fullname = fullname
    }

    fun getBio(): String
    {
        return bio
    }

    fun setBio(bio: String)
    {
        this.bio = bio
    }

    fun getCity(): String
    {
        return city
    }

    fun setCity(city: String)
    {
        this.city = city
    }

    fun getSpecialty(): String
    {
        return specialty
    }

    fun setSpecialty(specialty: String)
    {
        this.specialty = specialty
    }

    fun getBirthday(): String
    {
        return birthday
    }

    fun setBirthday(birthday: String)
    {
        this.birthday = birthday
    }

    fun getImage(): String
    {
        return image
    }

    fun setImage(image: String)
    {
        this.image = image
    }

    fun getUID(): String
    {
        return uid
    }

    fun setUID(uid: String)
    {
        this.uid = uid
    }

    fun getStatus(): String?{
        return status
    }

    fun setStatus(status: String){
        this.status = status
    }

    fun getMP(): String?{
        return mp
    }

    fun setMP(mp: String){
        this.mp = mp
    }

    fun getEmoji(): String?{
        return emoji
    }

    fun setEmoj(emoji: String){
        this.emoji = emoji
    }

    fun getDeleted(): String?{
        return deleted
    }

    fun setDeleted(deleted: String){
        this.deleted = deleted
    }
}