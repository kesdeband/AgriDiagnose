package com.agrinett.agridiagnose.models;

public class DiseaseModel implements Comparable<DiseaseModel> {

    private String _diseaseId;
    private String _characteristicId;
    private String _scaleId;
    private double _probabilityValue;
    private String _reason;
    private int _sortStage;

    public String DiseaseId() {
        return _diseaseId;
    }

    public void DiseaseId(String _diseaseId) {
        this._diseaseId = _diseaseId;
    }

    public String CharacteristicId() {
        return _characteristicId;
    }

    public void CharacteristicId(String _characteristicId) {
        this._characteristicId = _characteristicId;
    }

    public String ScaleId() {
        return _scaleId;
    }

    public void ScaleId(String _scaleId) {
        this._scaleId = _scaleId;
    }

    public double ProbabilityValue() {
        return _probabilityValue;
    }

    public void ProbabilityValue(double _probabilityValue) {
        this._probabilityValue = _probabilityValue;
    }

    public String Reason() {
        return _reason;
    }

    public void Reason(String _reason) {
        this._reason = _reason;
    }

    public int SortStage() {
        return _sortStage;
    }

    public void SortStage(int _sortStage) {
        this._sortStage = _sortStage;
    }

    @Override
    public int compareTo(DiseaseModel model) {
        return this.SortStage() - model.SortStage();
    }
}
