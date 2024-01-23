package Model;

/**
 * interface con el método que retorna las propiedades de la clase
 * */
public interface ModelMethods {

    /**
     * crea un String con las propiedades de la clase 
     * @return String con las propiedades ordenadas
    */
    public String getAllProperties();

    /**
     * crea un HashMap de Strings con las propiedades del modelo de base de datos
     * @return HashMap con el modelo de la base de datos
     */
    public String initModel();

}
