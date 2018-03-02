package com.agrinett.agridiagnose.models;

public class Characteristic {

    private String _characteristicId;
    private String _type;
    private String _question;
    private String _responseType;
    private String _groupParent;
    private int _questionOrder;

    public String CharacteristicId() {
        return _characteristicId;
    }

    public void CharacteristicId(String characteristicId) {
        this._characteristicId = characteristicId;
    }

    public String Type() {
        return _type;
    }

    public void Type(String type) {
        this._type = type;
    }

    public String Question() {
        return _question;
    }

    public void Question(String question) {
        this._question = question;
    }

    public String ResponseType() {
        return _responseType;
    }

    public void ResponseType(String responseType) {
        this._responseType = responseType;
    }

    public String GroupParent() {
        return _groupParent;
    }

    public void GroupParent(String groupParent) {
        this._groupParent = groupParent;
    }

    public int QuestionOrder() {
        return _questionOrder;
    }

    public void QuestionOrder(int questionOrder) {
        this._questionOrder = questionOrder;
    }
}
