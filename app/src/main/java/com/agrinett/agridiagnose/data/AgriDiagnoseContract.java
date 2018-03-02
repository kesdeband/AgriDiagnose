package com.agrinett.agridiagnose.data;

public final class AgriDiagnoseContract {
    public static final class CharacteristicsEntry {
        // Table name
        public static final String TABLE_NAME = "Characteristics";
        // Column (field) names
        static final String CHARACTERISTIC_ID = "CharacteristicId";
        static final String COMMON_NAME = "CommonName";
        static final String SCIENTIFIC_NAME = "ScientificName";
        static final String TYPE = "Type";
        static final String QUESTION = "Question";
        static final String RESPONSE_TYPE = "ResponseType";
        static final String GROUP_PARENT = "GroupParent";
        static final String QUESTION_ORDER = "QuestionOrder";
    }

    public static final class DiseaseModelEntry {
        // Table name
        public static final String TABLE_NAME = "DiseaseModels";
        // Column (field) names
        static final String DISEASE_ID = "DiseaseId";
        static final String TYPE = "Type";
        static final String DESCRIPTION = "Description";
        static final String SCIENTIFIC_NAME = "ScientificName";
        static final String LEVEL = "Level";
        static final String PARENT_ID = "ParentId";
        static final String REFERENCE = "Reference";
        static final String NOTES = "Notes";
    }

    public static final class DiseaseModelDetailsEntry {
        // Table name
        public static final String TABLE_NAME = "DiseaseModelDetails";
        // Column (field) names
        static final String DISEASE_ID = "DiseaseId";
        static final String CHARACTERISTIC_ID = "CharacteristicId";
        static final String SCALE_ID = "ScaleId";
        static final String PROBABILITY_VALUE = "ProbabilityValue";
        static final String SCALE_MODEL = "ScaleModel";
        static final String SCALE_EQUATION = "ScaleEquation";
        static final String BEST = "Best";
        static final String WORST = "Worst";
        static final String REASON = "Reason";
        static final String UNITS = "Units";
        static final String AHP = "Ahp";
        static final String SORT_STAGE = "SortStage";
    }

    public static final class ScalesEntry {
        // Table name
        public static final String TABLE_NAME = "Scales";
        // Column (field) names
        static final String SCALES_ID = "ScaleId";
        static final String SCALES_TYPE = "ScaleType";
        static final String DESCRIPTION = "Description";
        static final String CONTEXT = "Context";
    }

    public static final class QualitativeScaleEntry {
        // Table name
        public static final String TABLE_NAME = "QualitativeScale";
        // Column (field) names
        static final String SCALES_ID = "ScaleId";
        static final String PROPERTY_ID = "PropertyId";
        static final String PROPERTY = "Property";
    }

    public static final class QualitativeScaleValuesEntry {
        public static final String TABLE_NAME = "QualitativeScaleValues";
        // Column (field) names
        static final String CHARACTERISTIC_ID = "CharacteristicId";
        static final String SCALES_ID = "ScaleId";
        static final String DISEASE_ID = "DiseaseId";
        static final String PROPERTY_ID = "PropertyId";
        static final String PROPERTY = "Property";
        static final String PROPERTY_VALUE = "PropertyValue";
    }
}
