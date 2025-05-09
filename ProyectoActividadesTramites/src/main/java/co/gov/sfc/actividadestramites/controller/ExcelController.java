package co.gov.sfc.actividadestramites.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import co.gov.sfc.actividadestramites.service.ExcelService;

@RestController
@RequestMapping("/api")
public class ExcelController {

    @Autowired
    private ExcelService excelService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate; 

    @PostMapping("/importar")
    public ResponseEntity<String> importarExcel() {
        try {
            excelService.importarDatos();
            return ResponseEntity.ok("Importación exitosa.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en importación: " + e.getMessage());
        }
    }
    
    @GetMapping("/actividadesvigentes")
    public ResponseEntity<List<Map<String, Object>>> verActividadesVigentes() {
        String sql = "SELECT expediente, codigo_tramite, nombre_tramite, fecha_radicado_cero, " +
                     "fecha_ultimo_derivado, descripcion_ultimo_derivado, nombre_actividad, " +
                     "fecha_creacion_actividad, responsable_actividad, " +
                     "TRUNC(SYSDATE - fecha_radicado_cero) AS dias_desde_radicado " +
                     "FROM actividadesvigentes ORDER BY dias_desde_radicado DESC";

        List<Map<String, Object>> resultados = jdbcTemplate.queryForList(sql);
        return ResponseEntity.ok(resultados);
    }
}
