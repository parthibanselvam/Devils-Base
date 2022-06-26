package org.ets.core.services.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ExcelToJsonServiceImpl {

    private static final String XLSX = "xlsx";
    private static final String XLS = "xls";
    private static final String JSON_MIME_TYPE = "application/json";
    private static final String JSON_FILE_EXTENSION = ".json";
    private static final String SUB_SERVICE_NAME = "ets-resource-resolver";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelToJsonServiceImpl.class);

    public Workbook getWorkbook(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        final boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        Workbook workbook = null;
        if(isMultipart) {
             /*Getting XLS parameters*/
            final Map<String, RequestParameter[]> params = request.getRequestParameterMap();
            RequestParameter[] parameterArray = params.get("xls");
            RequestParameter file = parameterArray[0];
            String mimeType = FilenameUtils.getExtension(file.getFileName());
            LOGGER.debug("MimeType :{}", mimeType);
            if (mimeType!=null && mimeType.startsWith("xls")) {
                workbook = convertStreamToWorkbook(response, workbook, file, mimeType);
            }else {
                sendStatus(response,HttpServletResponse.SC_BAD_REQUEST,"Empty file!");
            }
        }
        return workbook;
    }


    private Workbook convertStreamToWorkbook(SlingHttpServletResponse response, Workbook workbook,RequestParameter file, String mimeType) {
        try (final InputStream stream = file.getInputStream()) {
            if (Objects.nonNull(stream) && StringUtils.isNotBlank(mimeType)) {
                if (mimeType.equals(XLSX)) {
                    workbook = new XSSFWorkbook(stream);
                } else if (mimeType.equals(XLS)) {
                    workbook = new HSSFWorkbook(stream);
                } else {
                    LOGGER.error("Unsupported file type : {}", mimeType);
                    sendStatus(response,HttpServletResponse.SC_BAD_REQUEST,mimeType.concat(" Unsupported file type!"));
                }
            }
        }
        catch(Exception e) {
            LOGGER.error("Exception in input stream to workbook conversion {}",e.getMessage());
        }
        return workbook;
    }

    public void dataExtractor(SlingHttpServletRequest request, SlingHttpServletResponse response, ResourceResolver resourceResolver, Workbook workbook)
            throws IOException {
         /*Getting parameters*/
        final Map<String, RequestParameter[]> params = request.getRequestParameterMap();
        String destinationPath = request.getParameter("destPath");
        String funFileName = request.getParameter("functionSelect");
        if (StringUtils.isNotBlank(destinationPath) && StringUtils.isNotBlank(funFileName)) {
            InputStream jsonStream = null;
            try {
                    //Get list of map objects
                    if( funFileName.equalsIgnoreCase("scorerequirement")) {
                        ArrayList<HashMap<String, String>> excelData = readExcel(workbook, 0,funFileName);
                        Map<String, Object> mapObj = new HashMap<>();
                        mapObj.put("scores", excelData);
                        if (workbook.getNumberOfSheets()>1 && workbook.getSheetName(1).equalsIgnoreCase("Footnote Text")) {
                            ArrayList<HashMap<String, String>> excelData1 = readExcel(workbook, 1,"Footnote Text");
                            Map<String, Object> mapObj1 = new HashMap<>();
                            mapObj1.put("footnotes", excelData1);
                            mapObj.putAll(mapObj1);
                            if (excelData == null && excelData1 == null) {
                                sendStatus(
                                        response,
                                        HttpServletResponse.SC_BAD_REQUEST,
                                        "Error in Column Header!"
                                );
                            } else {
                                Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
                                String parsedJSON = prettyGson.toJson(mapObj);
                    /*
                      Convert JSON array to stream
                      ** Keep encoding as UTF-8 **
                    */
					jsonStream = new ByteArrayInputStream(parsedJSON.getBytes(StandardCharsets.UTF_8));
                                LOGGER.info("Json is " + jsonStream);
                                //store stream in JCR as AEM asset
                                Asset fileInJCR =
                                        storeFileInJCR(
                                                destinationPath,
                                                funFileName.concat(JSON_FILE_EXTENSION),
                                                jsonStream,resourceResolver);

                                int statusCode = Objects.nonNull(fileInJCR)
                                        ? HttpServletResponse.SC_OK
                                        : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                                String message = Objects.nonNull(fileInJCR)
                                        ? "Successfully parsed the file!"
                                        : "Error occurred while saving the file! Check system user permissions";
                                sendStatus(response, statusCode, message);

                            }
                        }
                        else {
                            sendStatus(response,HttpServletResponse.SC_BAD_REQUEST,"Footnote Text Sheet is not there !");
                        }
                    }
                    else if(funFileName.equalsIgnoreCase("essa")) {
                        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                            ArrayList<HashMap<String, String>> excelData = readExcel(workbook, i,funFileName);
                            Map<String, Object> mapObj = new HashMap<>();
                            mapObj.put("Test", excelData);
                            if (excelData == null) {
                                sendStatus(
                                        response,
                                        HttpServletResponse.SC_BAD_REQUEST,
                                        "Error in Column Header!"
                                );
                            } else {
                                Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
                                String parsedJSON = prettyGson.toJson(mapObj);
                    /*
                      Convert JSON array to stream
                      ** Keep encoding as UTF-8 **
                    */
                                jsonStream = new ByteArrayInputStream(parsedJSON.getBytes(StandardCharsets.UTF_8));
                                LOGGER.info("Json is {}",jsonStream);
                                //store stream in JCR as AEM asset
                                Asset fileInJCR =
                                        storeFileInJCR(
                                                destinationPath.concat(workbook.getSheetName(i).toLowerCase()),
                                                funFileName.concat("_").concat(workbook.getSheetName(i).toLowerCase()).concat("_test").concat(JSON_FILE_EXTENSION),
                                                jsonStream,resourceResolver);

                                int statusCode = Objects.nonNull(fileInJCR)
                                        ? HttpServletResponse.SC_OK
                                        : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                                String message = Objects.nonNull(fileInJCR)
                                        ? "Successfully parsed the file!"
                                        : "Error occurred while saving the file! Check system user permissions";
                                sendStatus(response, statusCode, message);

                            }
                        }
                    }
                    else if(funFileName.equalsIgnoreCase("mybest_scores_acceptance") || funFileName.equalsIgnoreCase("home_edition_acceptance") ||funFileName.equalsIgnoreCase("essential_acceptance")) {
                        ArrayList<HashMap<String, String>> excelData = readExcelForAcceptance(workbook, 0,funFileName);
                        Map<String, Object> mapObj = new HashMap<>();
                        mapObj.put(funFileName, excelData);
                        if (excelData == null) {
                            sendStatus(
                                    response,
                                    SlingHttpServletResponse.SC_BAD_REQUEST,
                                    "Error in Column Header!"
                            );
                        } else {
                            Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
                            String parsedJSON = prettyGson.toJson(mapObj);
                    /*
                      Convert JSON array to stream
                      ** Keep encoding as UTF-8 **
                    */
                            jsonStream = new ByteArrayInputStream(parsedJSON.getBytes(StandardCharsets.UTF_8));
                            LOGGER.info("Json is " + jsonStream);
                            //store stream in JCR as AEM asset
                            Asset fileInJCR =
                                    storeFileInJCR(
                                            destinationPath,
                                            funFileName.concat(JSON_FILE_EXTENSION),
                                            jsonStream,resourceResolver);

                            int statusCode = Objects.nonNull(fileInJCR)
                                    ? SlingHttpServletResponse.SC_OK
                                    : SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                            String message = Objects.nonNull(fileInJCR)
                                    ? "Successfully parsed the file!"
                                    : "Error " +
                                    "occurred while saving the file! Check system user permissions";
                            sendStatus(response, statusCode, message);

                        }
                    }
                    else  {
                        ArrayList<HashMap<String, String>> excelData = readExcel(workbook, 0,funFileName);
                        Map<String, Object> mapObj = new HashMap<>();
                        mapObj.put(funFileName, excelData);
                        if (excelData == null) {
                            sendStatus(
                                    response,
                                    SlingHttpServletResponse.SC_BAD_REQUEST,
                                    "Error in Column Header!"
                            );
                        } else {
                            Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
                            String parsedJSON = prettyGson.toJson(mapObj);
                    /*
                      Convert JSON array to stream
                      ** Keep encoding as UTF-8 **
                    */
					jsonStream = new ByteArrayInputStream(parsedJSON.getBytes(StandardCharsets.UTF_8));
                            LOGGER.info("Json is " + jsonStream);


                            //store stream in JCR as AEM asset
                            Asset fileInJCR =
                                    storeFileInJCR(
                                            destinationPath,
                                            funFileName.concat(JSON_FILE_EXTENSION),
                                            jsonStream,resourceResolver);

                            int statusCode = Objects.nonNull(fileInJCR)
                                    ? SlingHttpServletResponse.SC_OK
                                    : SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                            String message = Objects.nonNull(fileInJCR)
                                    ? "Successfully parsed the file!"
                                    : "Error " +
                                    "occurred while saving the file! Check system user permissions";
                            sendStatus(response, statusCode, message);

                        }
                    }
            } catch (LoginException e) {
                LOGGER.error("Error Occurred: {}", e.getMessage());
                sendStatus(
                        response,
                        SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Error occurred while saving the file!"
                );
            } finally {
                if (Objects.nonNull(workbook) && Objects.nonNull(jsonStream)) {
                    workbook.close();
                    jsonStream.close();
                }
            }
        } else {
            sendStatus(
                    response,
                    SlingHttpServletResponse.SC_BAD_REQUEST,
                    "Unsupported request payload!"
            );
        }
    }

    private void sendStatus(SlingHttpServletResponse response, int status, String message) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.setContentType("text/plain");
        response.setStatus(status);
        response.getWriter().print(message);
    }

    // Read Excel File
    private ArrayList<HashMap<String, String>> readExcel(Workbook workbook, int sheetnumber,String funFileName) {
        ArrayList<HashMap<String, String>> resultList = new ArrayList<>();
        String[] tableHeader = getTableHeader(workbook,sheetnumber,funFileName);
        Sheet sheet = workbook.getSheetAt(sheetnumber);
        if(tableHeader!=null) {
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                HashMap<String, String> rowData = new HashMap<>();
                Iterator<Cell> cellIterator = row.cellIterator();
                for (int index = 0; index<tableHeader.length&&cellIterator.hasNext(); index++) {
                    Cell cell = cellIterator.next();
                    if (cell.getCellType() == CellType.NUMERIC) {
                        DataFormatter formatter = new DataFormatter();
                        String val = formatter.formatCellValue(cell);
                        if(val.isEmpty())
                        {
                            continue;
                        }
                        else {
                        rowData.put(tableHeader[cell.getColumnIndex()], val);}
                    } else {
                        if(cell.getRichStringCellValue().getString().isEmpty())
                        {
                            continue;
                        }
                        else
                        {
                            rowData.put(tableHeader[cell.getColumnIndex()], cell.getRichStringCellValue().getString());
                        }
                    }
                }
                if (!rowData.isEmpty()) {
                    resultList.add(rowData);
                }
            }
        }
        else
        {
            return null;
        }
        return resultList;
    }
	private ArrayList<HashMap<String, String>> readExcelForAcceptance(Workbook workbook, int sheetnumber,String funFileName) {
        ArrayList<HashMap<String, String>> resultList = new ArrayList<>();
        String[] tableHeader = getTableHeader(workbook,sheetnumber,funFileName);
        Sheet sheet = workbook.getSheetAt(sheetnumber);
        if(tableHeader!=null) {
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                HashMap<String, String> rowData = new HashMap<>();
                Iterator<Cell> cellIterator = row.cellIterator();
                for (int index = 0; index<tableHeader.length&&cellIterator.hasNext(); index++) {
                    Cell cell = cellIterator.next();
                    if (cell.getCellType() == CellType.NUMERIC) {
                        DataFormatter formatter = new DataFormatter();
                        String val = formatter.formatCellValue(cell);
                        if(val.isEmpty())
                        {
                            rowData.put(tableHeader[cell.getColumnIndex()], "");
                        }
                        else {
                        rowData.put(tableHeader[cell.getColumnIndex()], val);}
                    } else {
                        if(cell.getRichStringCellValue().getString().isEmpty())
                        {
                            rowData.put(tableHeader[cell.getColumnIndex()], "");
                        }
                        else
                        {
                            rowData.put(tableHeader[cell.getColumnIndex()], cell.getRichStringCellValue().getString());
                        }
                    }
                }
                if (!rowData.isEmpty()) {
                    resultList.add(rowData);
                }
            }
        }
        else
        {
            return null;
        }
        return resultList;
    }
    // Get table's header / first row
    private String[] getTableHeader(Workbook workbook, int sheetnumber,String funFileName) {
        List<String> footnote = Arrays.asList("FootnoteID","Symbol","Symbol Name","FootnoteText");
        List<String> scorerequirement = Arrays.asList("Test Description","Display Test Code","Test Name","State","Passing Score","Footnote","Usage Status","Type","State Name","Date Score No Longer Required");
        List<String> scorereportcalendar = Arrays.asList("Test Name And Code","Start Date","End Date","Report Date");
        List<String> essa = Arrays.asList("programCode","programInfo","examId","testName","testDuration(in minutes)");
        List<String> mybest_scores_acceptance = Arrays.asList("Country","State/Province","Institution","Program");
        List<String> home_edition_acceptance = Arrays.asList("Country","State/Province","Institution","Program");
        List<String> essential_acceptance = Arrays.asList("Country","State/Province","Institution","Program");
        List<String> GRE_MBA_programs = Arrays.asList("Country","State","School");
        List<String> institutions_and_fellowship_sponsors = Arrays.asList("Country","States/Territories","Code","Name","Note");
        List<String> contact_us_list_gre = Arrays.asList("Title","Note","Note URL","Email","Phone","Toll-free Phone","TTY","Fax","National Office","Website Title","Website URL","Department","Department URL","Hours","Bureau of Credentialing","TDD Access","Office Hours","Mail","Physical Address","Address","CLO");
        List<String> contact_us_list_praxis = Arrays.asList("Title","Subtitle","Note","Email","Phone","Toll-free Phone","TTY","Fax","National Office","Website Title","Department","Hours","Bureau of Credentialing","TDD Access","Office Hours","Mail","Physical Address","Address","CLO");
        List<String> contact_us_list_toefl = Arrays.asList("Region","Title","Subtitle","Note","Note URL","Email","Phone","Toll-free Phone","Candidate Cares","TTY","Fax","National Office","Website Title","Website URL","Department","Department URL","Hours","Bureau of Credentialing","TDD Access","Office Hours","Mail","Physical Address","Address","CLO","Center");
        List<String> contact_us_list_sls = Arrays.asList("Title","Subtitle","Note","Note URL","Email","Phone","Toll-free Phone","TTY","Fax","National Office","Website Title","Website URL","Department","Department URL","Hours","Bureau of Credentialing","TDD Access","Office Hours","Mail","Physical Address","Address","CLO");
        Map<String, List<String>> funMap = new HashMap<>();
        funMap.put("Footnote Text",footnote);
        funMap.put("scorerequirement",scorerequirement);
        funMap.put("scorereportcalendar",scorereportcalendar);
        funMap.put("contact_us_list_gre",contact_us_list_gre);
        funMap.put("contact_us_list_praxis",contact_us_list_praxis);
        funMap.put("contact_us_list_toefl",contact_us_list_toefl);
        funMap.put("contact_us_list_sls", contact_us_list_sls);
        funMap.put("mybest_scores_acceptance",mybest_scores_acceptance);
        funMap.put("institutions_and_fellowship_sponsors",institutions_and_fellowship_sponsors);
        funMap.put("essa",essa);
        funMap.put("home_edition_acceptance",home_edition_acceptance);
        funMap.put("GRE_MBA_programs",GRE_MBA_programs);
        funMap.put("essential_acceptance",essential_acceptance);
        Sheet sheet = workbook.getSheetAt(sheetnumber);
        Iterator<Row> rowIterator = sheet.iterator();
        Row row = rowIterator.next();
        Iterator<Cell> cellIterator = row.cellIterator();
        String[] tableHeader=null;
        if(funMap.containsKey(funFileName)) {
            tableHeader = new String[funMap.get(funFileName).size()];
            for (int index = 0; cellIterator.hasNext()&&index<funMap.get(funFileName).size(); index++) {
                Cell cell = cellIterator.next();
                if(row.getLastCellNum()!=row.getPhysicalNumberOfCells() || cell.getRichStringCellValue().getString().isEmpty()) {
                    return null;
                }
                else if(funMap.get(funFileName).contains(cell.getRichStringCellValue().getString()))
                {
                tableHeader[index] = cell.getRichStringCellValue().getString();
                }
            }
        }
        return tableHeader;
    }

    // Store file in JCR
    private Asset storeFileInJCR(String destinationPath, String fileName, InputStream jsonStream, ResourceResolver resourceResolver)
            throws LoginException {
        Map<String, Object> serviceNameParam = new HashMap<>();
        serviceNameParam.put(ResourceResolverFactory.SUBSERVICE, SUB_SERVICE_NAME);
        AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
        if (Objects.nonNull(assetManager)) {
            return assetManager.createAsset(
                    destinationPath.concat("/".concat(fileName)),
                    jsonStream,
                    JSON_MIME_TYPE,
                    true);
        } else {
            return null;
        }
    }
}