package com.openjob.web.export;

import com.openjob.common.model.Job;
import com.openjob.web.dto.ExportCvDTO;
import com.openjob.web.dto.ExportDTO;
import com.openjob.web.job.JobService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/export")
public class ExportController {
    private final JobService jobService;

    @PostMapping("/accepted-cv")
    public ResponseEntity<?> exportAcceptedCv(@Valid @RequestBody ExportDTO body) throws IOException {
        Optional<Job> jobOptional = jobService.getById(body.getJobId());
        if (jobOptional.isEmpty())
            throw new IllegalArgumentException("Job not found for ID: " + body.getJobId());
        Job job = jobOptional.get();
        String cvFrom;
        String jobTitle = job.getTitle();
        String specialization = job.getSpecialization().getName();
        String date = job.getCreatedAt().toString();
        List<ExportCvDTO> listCvDto;

        if (Objects.nonNull(body.getAppliedCVs())){
            cvFrom = "Applied CVs";
            listCvDto = body.getAppliedCVs();
        } else {
            cvFrom = "Matched CVs";
            listCvDto = body.getMatchedCVs();
        }
        // Write to file
        String filename = "";
        Workbook wb;
        try (InputStream is = new FileInputStream(new File("").getAbsolutePath() + "/openjob-web-api/src/main/resources/template/export_cv_template.xlsx")){
            wb = WorkbookFactory.create(is);
            Sheet sheet = wb.getSheetAt(0);
            //Write job info
            Row row = sheet.getRow(0);
            Cell cell = row.createCell(2);
            cell.setCellValue(cvFrom);
            row = sheet.getRow(1);
            cell = row.createCell(2);
            cell.setCellValue(jobTitle);
            row = sheet.getRow(2);
            cell = row.createCell(2);
            cell.setCellValue(specialization);
            row = sheet.getRow(3);
            cell = row.createCell(2);
            cell.setCellValue(date);
            // write list cv info
            int rowCount = 6;
            for (ExportCvDTO obj : listCvDto){
                row = sheet.createRow(rowCount++);
                cell = row.createCell(0);
                cell.setCellValue(obj.getFirstName());
                cell = row.createCell(1);
                cell.setCellValue(obj.getLastName());
                cell = row.createCell(2);
                cell.setCellValue(obj.getEmail());
                cell = row.createCell(3);
                cell.setCellValue(obj.getPhone());
                cell = row.createCell(4);
                cell.setCellValue(obj.getGender());
                cell = row.createCell(5);
                cell.setCellValue(obj.getUrl());
            }

            for (int i=0; i<6; i++){
                sheet.autoSizeColumn(i);
            }
//             Save the Workbook
            File saveFolder = new File(new File("..").getAbsolutePath() +"\\exported-cv");
            if (!saveFolder.exists())
                saveFolder.mkdirs();
            filename = new File("..").getAbsolutePath() +"\\exported-cv\\Accepted_CV_" + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + ".xlsx" ;
            OutputStream os = new FileOutputStream(filename);
            wb.write(os);
            os.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        return ResponseEntity.ok(filename);


//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        new ObjectOutputStream( baos ).writeObject( wb );
//        return ResponseEntity.ok(Base64.getEncoder().encode(baos.toByteArray()));
    }

    @GetMapping(path = "/download")
    public ResponseEntity<?> downloadFile(@RequestParam("filename")String filename) throws FileNotFoundException {
        File file = new File(filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }
}
