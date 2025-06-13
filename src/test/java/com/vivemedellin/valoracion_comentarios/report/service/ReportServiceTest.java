package com.vivemedellin.valoracion_comentarios.report.service;

import com.vivemedellin.valoracion_comentarios.report.dto.CreateReportDto;
import com.vivemedellin.valoracion_comentarios.report.dto.ReportDto;
import com.vivemedellin.valoracion_comentarios.report.entity.Report;
import com.vivemedellin.valoracion_comentarios.report.factory.ReportFactory;
import com.vivemedellin.valoracion_comentarios.report.mapper.ReportMapper;
import com.vivemedellin.valoracion_comentarios.report.repository.ReportRepository;
import com.vivemedellin.valoracion_comentarios.report.entity.ReportReason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    private ReportMapper reportMapper;
    private ReportFactory reportFactory;
    private ReportRepository reportRepository;
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportMapper = mock(ReportMapper.class);
        reportFactory = mock(ReportFactory.class);
        reportRepository = mock(ReportRepository.class);
        reportService = new ReportService(reportMapper, reportFactory, reportRepository);
    }

    @Test
    void createReport_shouldReturnMappedDto() {
        CreateReportDto input = new CreateReportDto(ReportReason.SPAM, 123L);
        Report report = new Report();
        Report savedReport = new Report();
        ReportDto expectedDto = new ReportDto();

        when(reportFactory.createFromDto(input)).thenReturn(report);
        when(reportRepository.save(report)).thenReturn(savedReport);
        when(reportMapper.toDto(savedReport)).thenReturn(expectedDto);

        ReportDto result = reportService.createReport(input);

        assertEquals(expectedDto, result);
        verify(reportFactory).createFromDto(input);
        verify(reportRepository).save(report);
        verify(reportMapper).toDto(savedReport);
    }


    @Test
    void getReportsByAdminId_shouldReturnMappedDtos() {
        UUID adminId = UUID.randomUUID();
        Report report1 = new Report();
        Report report2 = new Report();
        ReportDto dto1 = new ReportDto();
        ReportDto dto2 = new ReportDto();

        List<Report> reports = List.of(report1, report2);
        List<ReportDto> expectedDtos = List.of(dto1, dto2);

        when(reportRepository.findAllByAdminId(adminId)).thenReturn(reports);
        when(reportMapper.toDto(report1)).thenReturn(dto1);
        when(reportMapper.toDto(report2)).thenReturn(dto2);

        List<ReportDto> result = reportService.getReportsByAdminId(adminId);

        assertEquals(expectedDtos, result);
        verify(reportRepository).findAllByAdminId(adminId);
        verify(reportMapper).toDto(report1);
        verify(reportMapper).toDto(report2);
    }
}
