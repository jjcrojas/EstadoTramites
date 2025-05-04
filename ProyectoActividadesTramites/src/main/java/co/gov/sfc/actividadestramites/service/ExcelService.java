package co.gov.sfc.actividadestramites.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

@Service
public class ExcelService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Value("${app.excel-folder}")
	private String excelFolder;

	public void importarDatos() throws Exception {
		String archivo = excelFolder + "/Informe de Actividades Vigentes.xlsx";
		FileInputStream fis = new FileInputStream(new File(archivo));
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		Sheet sheet = workbook.getSheetAt(0);

		jdbcTemplate.update("DELETE FROM actividadesvigentes_tmp");

		for (int i = 9; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row == null)
				continue;
			int filaExcel = row.getRowNum() + 1;

			boolean filaVacia = true;

			for (int j = 0; j <= 13; j++) {
				Cell celda = row.getCell(j);
				if (celda != null && !celda.toString().trim().isEmpty()) {
					filaVacia = false;
					break;
				}
			}

			if (filaVacia) {
//				System.out.println("⚠️ Fila vacía detectada en la fila Excel: " + (row.getRowNum() + 1));
				continue; // omitir esta fila
			} else {

				jdbcTemplate.update("INSERT INTO actividadesvigentes_tmp ("
						+ "expediente, dependencia, fecha_radicado_cero, codigo_tramite, nombre_tramite, "
						+ "ultimo_derivado, fecha_ultimo_derivado, descripcion_ultimo_derivado, nombre_actividad, "
						+ "fecha_creacion_actividad, responsable_actividad, nombre_tercero, proceso, dias_creacion_actividad, fila_excel"
						+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", getString(row, 0), getString(row, 1),
						getDate(row, 2), getString(row, 3), getString(row, 4), getString(row, 5), getDate(row, 6),
						getString(row, 7), getString(row, 8), getDate(row, 9), getString(row, 10), getString(row, 11),
						getString(row, 12), getDias(row, 13), filaExcel);
			}
		}

		workbook.close();

		jdbcTemplate.update("""
	            INSERT INTO actividadesvigentes (
	                expediente, dependencia, fecha_radicado_cero, codigo_tramite, nombre_tramite,
	                ultimo_derivado, fecha_ultimo_derivado, descripcion_ultimo_derivado, nombre_actividad,
	                fecha_creacion_actividad, responsable_actividad, nombre_tercero, proceso, dias_creacion_actividad
	            )
	            SELECT expediente, dependencia, fecha_radicado_cero, codigo_tramite, nombre_tramite,
	                   ultimo_derivado, fecha_ultimo_derivado, descripcion_ultimo_derivado, nombre_actividad,
	                   fecha_creacion_actividad, responsable_actividad, nombre_tercero, proceso, dias_creacion_actividad
	            FROM (
	                SELECT t.*, ROW_NUMBER() OVER (
	                    PARTITION BY expediente
	                    ORDER BY fecha_creacion_actividad DESC
	                ) rn
	                FROM actividadesvigentes_tmp t
	                WHERE dependencia LIKE '41%'
	            ) sub
	            WHERE sub.rn = 1
	        """);
	}

	private String getString(Row row, int cellNum) {
		Cell cell = row.getCell(cellNum);
		return cell != null ? cell.toString().trim() : null;
	}

	private Date getDate(Row row, int cellNum) {
		Cell cell = row.getCell(cellNum);
		if (cell != null && cell.getCellType() == CellType.NUMERIC) {
			return cell.getDateCellValue();
		}
		return null;
	}

	private Integer getDias(Row row, int cellNum) {
		try {
			String value = getString(row, cellNum);
			if (value != null && value.contains("\n")) {
				value = value.split("\n")[0];
			}
			return Integer.valueOf(value.replaceAll("[^0-9]", ""));
		} catch (Exception e) {
			return null;
		}
	}
}
