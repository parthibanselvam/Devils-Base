package org.ets.core.services.impl;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.ets.core.servlets.ExcelToJSONServlet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({AemContextExtension.class,MockitoExtension.class })
class ExcelToJsonServiceTest {

    private final AemContext aemContext = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    ExcelToJSONServlet testServlet = new ExcelToJSONServlet();

    private Map<String, Object> parameterMap = new HashMap<String, Object>();

    @Test
    void dryRun() throws IOException {
        /* Instantiate Service */
        ExcelToJsonServiceImpl excelToJsonService = aemContext.registerService(new ExcelToJsonServiceImpl());
        /* Get mock request and response from aemContext */
        MockSlingHttpServletRequest mockSlingRequest = aemContext.request();
        MockSlingHttpServletResponse mockSlingResponse = aemContext.response();
        ResourceResolver resourceResolver = aemContext.resourceResolver();

        File file = new File("src/test/resources/org/ets/core/servlets/empty-file.xlsx");
        FileInputStream input = new FileInputStream(file);
        Workbook mockWorkbook = mock(Workbook.class);
        Sheet mockSheet = mock(Sheet.class);
        Row mockRow = mock(Row.class);
        ArrayList<Row> RowList = new ArrayList<>();
        RowList.add(mockRow);
        Iterator<Row> rowIterator= RowList.iterator();
        Cell mockCell = mock(Cell.class);
        ArrayList<Cell> CellList = new ArrayList<>();
        CellList.add(mockCell);
        Iterator<Cell> cellIterator= CellList.iterator();
        when(mockWorkbook.getSheetAt(anyInt())).thenReturn(mockSheet);
        when(mockSheet.iterator()).thenReturn(rowIterator);
        when(mockRow.cellIterator()).thenReturn(cellIterator);

        /* Prepare mock request object */
        mockSlingRequest.setContentType("multipart/form-data");
        mockSlingRequest.setMethod("POST");
        parameterMap.put("xls", input);
        parameterMap.put("destPath", "/content/dam/json");
        parameterMap.put("functionSelect", "sample-text");
        mockSlingRequest.setParameterMap(parameterMap);

        Workbook workbook=excelToJsonService.getWorkbook(mockSlingRequest,mockSlingResponse);
        assertNull(workbook);
        excelToJsonService.dataExtractor(mockSlingRequest, mockSlingResponse, resourceResolver, mockWorkbook);

    }

}