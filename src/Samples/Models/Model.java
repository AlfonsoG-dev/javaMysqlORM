package Samples.Models;

import Model.ModelMethods;
import Mundo.TableProperties;
import Utils.ModelMetadata;

public class Model implements ModelMethods {

    @TableProperties(miConstraint = "not null unique primary key auto_increment", miType = "int")
    private int id_pk;
    @TableProperties(miConstraint = "not null", miType = "text")
    private String description;

    public Model(int id_pk, String description) {
        this.id_pk = id_pk;
        this.description = description;
    }

    public Model(String description) {
        this.description = description;
    }

    public int getId_pk() {
        return id_pk;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getAllProperties() {
        StringBuffer b = new StringBuffer();
        if(getId_pk() > 0) {
            b.append("id_pk: ");
            b.append(getId_pk());
            b.append("\n");
        }
        if(!getDescription().isEmpty() || getDescription() != null) {
            b.append("description: ");
            b.append(getDescription());
        }
        return b.toString();
    }

    @Override
    public String initModel() {
        ModelMetadata metadata = new ModelMetadata("Samples.Models.Model");
        return metadata.getModelProperties();
    }
}
