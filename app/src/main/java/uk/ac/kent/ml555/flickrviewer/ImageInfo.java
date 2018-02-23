package uk.ac.kent.ml555.flickrviewer;


public class ImageInfo {
    String title;
    int imageResource;
    String id;
    String description;
    String url_m;
    String url_l;
    String url_o;
    String owner;



    public String getLargeImage(){
        String x;
        if(url_l != null){
            x = url_l;
        }else if(url_o != null){
            x = url_o;
        }else{
            x = url_m;
        }
        return x;
    }

    public String getTitle(){
        String x = "Title not found";
        if(!title.isEmpty()){
            return title;
        }else
            return x ;
    }


    public  String getDescription(){
        String x = "Description not found";
        if(!description.isEmpty()){
            return description;
        }else
            return x ;
    }


}
