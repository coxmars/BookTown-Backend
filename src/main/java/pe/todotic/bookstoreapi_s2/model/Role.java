package pe.todotic.bookstoreapi_s2.model;

// Por defecto en un enum se usan los indices iniciando desde 0, pero en el model debe indicarse con una
// anotacion que tambien se puede recibir en string, como ADMIN/USER. Importante saberlo.
public enum Role {
    ADMIN, // 0
    USER // 1
}
