package co.gov.sfc.actividadestramites.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class ActividadesViewController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/actividades")
    public String verActividadesWeb(Model model) {
        String sql = "SELECT expediente, dependencia, fecha_radicado_cero, codigo_tramite, nombre_tramite, " +
                "ultimo_derivado, fecha_ultimo_derivado, descripcion_ultimo_derivado, nombre_actividad, " +
                "fecha_creacion_actividad, responsable_actividad, nombre_tercero, proceso, " +
                "TRUNC(SYSDATE - fecha_creacion_actividad) AS dias_creacion_actividad, " +
                "CASE " +
                "   WHEN (SYSDATE - fecha_creacion_actividad) < 30 THEN 'Menos de 30 días' " +
                "   WHEN (SYSDATE - fecha_creacion_actividad) BETWEEN 30 AND 60 THEN 'Entre 30 y 60 días' " +
                "   ELSE 'Más de 60 días' " +
                "END AS rango_dias " +
                "FROM actividadesvigentes " +
                "ORDER BY dias_creacion_actividad DESC";

        List<Map<String, Object>> actividades = jdbcTemplate.queryForList(sql);
        model.addAttribute("actividades", actividades);
        return "actividades-vigentes";
    }
}

