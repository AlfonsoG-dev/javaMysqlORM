package Utils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;


/**
 * clase para obtener las propiedades del modelo
 */
public class ModelMetadata {
    /**
     * nombre del modelo
     */
    private String modelName;
    /**
     * constructor
     */
    public ModelMetadata(String nModelName) {
        modelName = nModelName;
    }
    /**
     * lista de campos de la clase
     * <br> pre: </br> los campos de la clase deben ser `public`
     * @param model: nombre de la clase
     * @throws ClassNotFoundException: error al buscar la clase
     * @return la lista de los campos de la clase
     */
    private Field[] GetModelFiedls() throws ClassNotFoundException {
        Class<?> miClase = Class.forName(this.modelName);
        Field[] myFields = miClase.getDeclaredFields();
        return myFields;
    }
    /**
     * generar las columnas del modelo en base a los campos del mismo
     * @param model: nombre del modelo
     * @return las columnas del modelo
     */
    private String GetModelColumns() {
        String resultado = "";
        try {
            Field[] myFields = this.GetModelFiedls();
            if(myFields.length > 0) {
                for(Field f: myFields) {
                    resultado += f.getName() + ", ";
                }
            }
        } catch(ClassNotFoundException e) {
            System.err.println(e);
        }
        String clean_r = resultado.substring(0, resultado.length()-2);
        return clean_r;
    }
    /**
     * genera una lista de annotations del modelo
     * @return la lista de annotations del modelo
     */
    private ArrayList<Annotation[]> GetAnotations() {
        ArrayList<Annotation[]> resultado = new ArrayList<>();
        try {
            Field[] myFields = this.GetModelFiedls();
            if(myFields.length > 0) {
                for(Field f: myFields) {
                    if(f.getAnnotations().length > 0) {
                        resultado.add(f.getAnnotations());
                    }
                }
            }
        } catch(ClassNotFoundException e) {
            System.err.println(e);
        }
        return resultado;
    }
    /**
     * genera un String con los datos de las annotations segun un tipo
     * @param type: el tipo de annotations a generar
     * @return un String con los datos de las annotations
     */
    private String GetAnnotationConstraint() {
        String constraint = "";
        ArrayList<Annotation[]> misAnnotations = this.GetAnotations();
        for(Annotation[] m: misAnnotations) {
            constraint += m[0] + " and ";
        }
        String clean_constraint = constraint.substring(0, constraint.length()-5);
        return clean_constraint;
    }
    /**
     * genera un String con el constraint de cada columna en orden
     * <br> pre: </br> el orden en el que se crean los datos debe ser el mismo de las annotations
     * @return un String con los constraint de cada columna
     */
    private String GetModelColumConstraint() {
        String resultado = "";
        try {
            String[] f_constraint = this.GetAnnotationConstraint().split(" and ");
            String datos = "";
            for(String c: f_constraint) {
                String[] f = c.split(".miConstraint");
                for(String i: f) {
                    String[] ci = i.split(", ");
                    if(ci[0].startsWith("\"") || ci[0].endsWith("\"")) {
                        datos += ci[0];
                    }
                }
            }
            String r_datos = datos.replace("=", ", ");
            String c_datos = r_datos.substring(2, r_datos.length());
            resultado = c_datos;
        } catch(Exception e) {
            System.err.println(e);
        }
        return resultado;
    }
    /**
     * genera un String con el tipo de dato de cada columna
     * <br> pre: </br> el orden en el que se crean los datos debe ser el mismo de las annotations
     * @return un String con el tipo de dato de cada columna
     */
    private String GetModelColumnType() {
        String resultado = "";
        try {
            String[] f_constraint = this.GetAnnotationConstraint().split(" and ");
            String datos = "";
            for(String c: f_constraint) {
                String[] f = c.split(".miConstraint");
                for(String i: f) {
                    String[] ci = i.split(", ");
                    for(String t: ci) {
                        String[] mt = t.split("miType=");
                        if(mt.length == 2) {
                            if(mt[1].startsWith("\"") || mt[1].endsWith(")")) {
                                datos += mt[1];
                            }
                        }
                    }
                }
            }
            String r_datos = datos.replace("\")", "\", ");
            String c_datos = r_datos.substring(0, r_datos.length()-2);
            resultado = c_datos;
        } catch(Exception e) {
            System.err.println(e);
        }
        return resultado;
    }
    /**
     * genera un String con los datos combinados del modelo
     * @return el string con los datos del modelo
     */
    public String GetModelProperties() {
        String build = "";
        String[] columns = this.GetModelColumns().split(", ");
        String[] types = this.GetModelColumnType().split(", ");
        String[] constraint = this.GetModelColumConstraint().split(", ");
        for(int i=0; i<columns.length; ++i) {
            if(constraint[i] != "") {
                build += columns[i] + ": " + types[i] + " " + constraint[i] + "\n";
            }
        }
        String clean_build = build.replace("\"", "");
        return clean_build;
    }
}
