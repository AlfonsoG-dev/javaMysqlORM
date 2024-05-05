package Model;

import Utils.Model.ModelMetadata;

/**
 * interface  used to getModelValues & model properties
 * */
public interface ModelMethods {

    /**
     * model values when using an instance.
    */
    public String getAllProperties();

    /**
     * model properties when using as table model reference.
     */
    public default String initModel() {
        ModelMetadata metadata = new ModelMetadata(this.getClass());
        return metadata.getModelProperties();
    }

}
