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
        String sql = "SELECT expediente, codigo_tramite, nombre_tramite, fecha_radicado_cero, " +
                     "fecha_ultimo_derivado, descripcion_ultimo_derivado, nombre_actividad, " +
                     "fecha_creacion_actividad, responsable_actividad, " +
                     "TRUNC(SYSDATE - fecha_radicado_cero) AS dias_desde_radicado " +
                     "FROM actividadesvigentes ORDER BY dias_desde_radicado DESC";

        List<Map<String, Object>> actividades = jdbcTemplate.queryForList(sql);
        model.addAttribute("actividades", actividades);
        return "actividades-vigentes";
    }
}

