USE PetSafe;

CREATE VIEW VistaNegociosProtectoras AS
SELECT
    u.id AS UsuarioID,
    u.nombreUser AS NombreUsuario,
    u.email AS EmailUsuario,
    u.telefonoContacto AS TelefonoContacto,
    n.idNegocio AS NegocioID,
    n.nombreNegocio AS NombreNegocio,
    n.direccion AS DireccionNegocio,
    n.descripcion AS DescripcionNegocio,
    n.fotos AS FotosNegocio,
    n.puntuacion AS PuntuacionNegocio,
    tn.nombre AS TipoNegocio,
    p.idProtectora AS ProtectoraID,
    p.nombreProtectora AS NombreProtectora,
    p.direccion AS DireccionProtectora,
    p.descripcion AS DescripcionProtectora,
    p.fotos AS FotosProtectora
FROM
    Usuario u
LEFT JOIN
    Negocio n ON u.id = n.Usuario_id
LEFT JOIN
    TipoNegocio tn ON n.tipoNegocio_id = tn.id
LEFT JOIN
    Protectoras p ON u.id = p.Usuario_id;

SELECT * FROM VistaNegociosProtectoras;
