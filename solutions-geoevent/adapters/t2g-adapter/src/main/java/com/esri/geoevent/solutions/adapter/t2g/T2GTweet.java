package com.esri.geoevent.solutions.adapter.t2g;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonAnySetter;

public class T2GTweet
{

  public static class Coordinates
  {
    private ArrayList<Double> _coordinates;
    private String            _type;

    public ArrayList<Double> getCoordinates()
    {
      return _coordinates;
    }

    public String getType()
    {
      return _type;
    }

    public void setCoordinates(ArrayList<Double> d)
    {
      _coordinates = d;
    }

    public void setType(String s)
    {
      _type = s;
    }

    @JsonAnySetter
    public void handleUnknown(String key, Object value)
    {
      // System.out.println("Unknown key found in coordinates: " + key);
    }
  }

  public static class BoundingBox
  {
    private ArrayList<ArrayList<ArrayList<Double>>> _coordinates;
    private String                                  _type;

    public ArrayList<ArrayList<ArrayList<Double>>> getCoordinates()
    {
      return _coordinates;
    }

    public void setCoordinates(ArrayList<ArrayList<ArrayList<Double>>> _coordinates)
    {
      this._coordinates = _coordinates;
    }

    public String getType()
    {
      return _type;
    }

    public void setType(String _type)
    {
      this._type = _type;
    }

    @JsonAnySetter
    public void handleUnknown(String key, Object value)
    {
      // System.out.println("Unknown key found in BoundingBox: " + key);
    }

  }

  public static class Place
  {
    private BoundingBox _bounding_box;
    private String      _country;
    private String      _country_code;
    private String      _full_name;
    private String      _id;
    private String      _name;
    private String      _place_type;
    private String      _url;

    public BoundingBox getBounding_box()
    {
      return _bounding_box;
    }

    public String getCountry()
    {
      return _country;
    }

    public void setBounding_box(BoundingBox _bounding_box)
    {
      this._bounding_box = _bounding_box;
    }

    public void setCountry(String _country)
    {
      this._country = _country;
    }

    public String getCountry_code()
    {
      return _country_code;
    }

    public void setCountry_code(String _country_code)
    {
      this._country_code = _country_code;
    }

    public String getFull_name()
    {
      return _full_name;
    }

    public void setFull_name(String _full_name)
    {
      this._full_name = _full_name;
    }

    public String getId()
    {
      return _id;
    }

    public void setId(String _id)
    {
      this._id = _id;
    }

    public String getName()
    {
      return _name;
    }

    public void setName(String _name)
    {
      this._name = _name;
    }

    public String getPlace_type()
    {
      return _place_type;
    }

    public void setPlace_type(String _place_type)
    {
      this._place_type = _place_type;
    }

    public String getUrl()
    {
      return _url;
    }

    public void setUrl(String _url)
    {
      this._url = _url;
    }

    @JsonAnySetter
    public void handleUnknown(String key, Object value)
    {
      // System.out.println("Unknown key found in Place: " + key);
    }
  }

  public static class User
  {
    private String _id_str;
    private String _name;
    private int    _followers_count;
    private String _location;
    private String _screen_name;

    public int getFollowers_count()
    {
      return _followers_count;
    }

    public void setFollowers_count(int _followers_count)
    {
      this._followers_count = _followers_count;
    }

    public String getLocation()
    {
      return _location;
    }

    public void setLocation(String _location)
    {
      this._location = _location;
    }

    public String getId_str()
    {
      return _id_str;
    }

    public void setId_str(String _id_str)
    {
      this._id_str = _id_str;
    }

    public String getName()
    {
      return _name;
    }

    public void setName(String _name)
    {
      this._name = _name;
    }

    public String getScreen_name()
    {
      return _screen_name;
    }

    public void setScreen_name(String _screen_name)
    {
      this._screen_name = _screen_name;
    }

    @JsonAnySetter
    public void handleUnknown(String key, Object value)
    {
      // System.out.println("Unknown key found in Place: " + key);
    }
  }

  private Boolean     _possibly_sensitive_editable;
  private String      _text;
  private String      _created_at;
  private Boolean     _retweeted;
  private Integer     _retweet_count;
  private Place       _place;
  private Coordinates _coordinates;
  private String      _id_str;
  private String      _in_reply_to_screen_name;
  private String      _in_reply_to_status_id_str;
  private Boolean     _favorited;
  private Boolean     _truncated;
  private Boolean     _possibly_sensitive;
  private String      _in_reply_to_user_id_str;
  private User        _user;
  public User getUser()
  {
    return _user;
  }

  public void setUser(User _user)
  {
    this._user = _user;
  }

  public Boolean getPossibly_sensitive_editable()
  {
    return _possibly_sensitive_editable;
  }

  public void setPossibly_sensitive_editable(Boolean _sensitiveEditable)
  {
    this._possibly_sensitive_editable = _sensitiveEditable;
  }

  public String getText()
  {
    return _text;
  }

  public void setText(String _text)
  {
    this._text = _text;
  }

  public String getCreated_at()
  {
    return _created_at;
  }

  public void setCreated_at(String _createdAt)
  {
    this._created_at = _createdAt;
  }

  public Boolean getRetweeted()
  {
    return _retweeted;
  }

  public void setRetweeted(Boolean _retweeted)
  {
    this._retweeted = _retweeted;
  }

  public Integer getRetweet_count()
  {
    return _retweet_count;
  }

  public void setRetweet_count(Integer _retweetCount)
  {
    this._retweet_count = _retweetCount;
  }

  public Place getPlace()
  {
    return _place;
  }

  public void setPlace(Place _place)
  {
    this._place = _place;
  }

  public Coordinates getCoordinates()
  {
    return _coordinates;
  }

  public void setCoordinates(Coordinates _coordinates)
  {
    this._coordinates = _coordinates;
  }

  public String getId_str()
  {
    return _id_str;
  }

  public void setId_str(String _id_str)
  {
    this._id_str = _id_str;
  }

  public String getIn_reply_to_screen_name()
  {
    return _in_reply_to_screen_name;
  }

  public void setIn_reply_to_screen_name(String _in_reply_to_screen_name)
  {
    this._in_reply_to_screen_name = _in_reply_to_screen_name;
  }

  public String getIn_reply_to_status_id_str()
  {
    return _in_reply_to_status_id_str;
  }

  public void setIn_reply_to_status_id_str(String _in_reply_to_status_id_str)
  {
    this._in_reply_to_status_id_str = _in_reply_to_status_id_str;
  }

  public Boolean getFavorited()
  {
    return _favorited;
  }

  public void setFavorited(Boolean _favorited)
  {
    this._favorited = _favorited;
  }

  public Boolean getTruncated()
  {
    return _truncated;
  }

  public void setTruncated(Boolean _truncated)
  {
    this._truncated = _truncated;
  }

  public Boolean getPossibly_sensitive()
  {
    return _possibly_sensitive;
  }

  public void setPossibly_sensitive(Boolean _possibly_sensitive)
  {
    this._possibly_sensitive = _possibly_sensitive;
  }

  public String getIn_reply_to_user_id_str()
  {
    return _in_reply_to_user_id_str;
  }

  public void setIn_reply_to_user_id_str(String _in_reply_to_user_id_str)
  {
    this._in_reply_to_user_id_str = _in_reply_to_user_id_str;
  }

  @JsonAnySetter
  public void handleUnknown(String key, Object value)
  {
    // System.out.println("Unknown key found in tweet: " + key);
  }
}


