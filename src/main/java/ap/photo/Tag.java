package ap.photo;

import java.util.UUID;

public class Tag{
    private String tagId ;
    private String name ;
    public Tag (String name){
        this.tagId= UUID.randomUUID().toString();
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