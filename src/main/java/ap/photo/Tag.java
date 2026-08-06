package ap.photo;

import java.util.UUID;

public class Tag{
    private String tagId ;
    private String name ;
    public Tag (String tagId , String name){
        this.tagId= tagId ;
        this.name = name ;
    }
    
    public String getTagId(){
        return tagId ;
    }
    public String getName(){
        return name ;
    }

    public void setName(String name){
        this.name=name ;
    }
}