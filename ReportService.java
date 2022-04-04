package go.pajak.pbb.app.registrasi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import go.pajak.pbb.app.registrasi.dtomodel.*;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

@Service
public class ReportService implements IReportService {
    @Autowired
    ResourceLoader resourceLoader;
    @Autowired
    @Qualifier("RegServiceWebClient")
    WebClient regWebClient;

    @Autowired
    QrCodeService qrCodeService;

    @Autowired
    ReferensiService referensiService;

    @Autowired
    ISktService sktService;

    @Autowired
    HcpService hcpService;

    @Autowired
    SigningService signingService;


    @Override
    public ByteArrayInputStream getSktReport(String idSkt, UserSikkaModel user) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //ResponseModel rm = getDataDetail(idSkt, kdKanwil, kdKpp);
            ResponseModel rm = sktService.getSkt(idSkt, user);
            ResponseModel rmKpp = referensiService.getKantorByKdKpp(user.getKodeKpp(), user.getKodeKanwil());
            if (rm.getKodeResponse() == 1 && rmKpp.getKodeResponse() == 1) {
                ObjectMapper mapper = new ObjectMapper();
                DtoKantorModel kantor = mapper.convertValue(rmKpp.getObjResponse(), DtoKantorModel.class);
                DtoSkt lim = (DtoSkt) rm.getObjResponse();
                PdfWriter.getInstance(document, out);
                document.open();

                //watermark
                Resource resource2 = resourceLoader.getResource("classpath:report_image/DRAFT.png");
                Image img2 = Image.getInstance(resource2.getURL());
                img2.setAbsolutePosition(document.getPageSize().getWidth() / 2 - 200, document.getPageSize().getHeight() / 2 - 85);

                //qrcode generator
                Image img4 = Image.getInstance(qrCodeService.generateQRCodeImage("PBBREG_" + lim.getSignedDocId() + "_" + lim.getNpwp()), null);

                // Add Text to PDF file ->
                Font font = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
                Font font1 = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.UNDERLINE, BaseColor.BLACK);
                Font font2 = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.ITALIC, BaseColor.BLACK);
                Font font3 = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
                Font font4 = FontFactory.getFont(FontFactory.HELVETICA, 13, BaseColor.BLACK);
                Font font5 = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);


                // Table 1
                PdfPTable t1 = new PdfPTable(2);
                t1.setWidthPercentage(95);
                // Set Each Column Width - Make Sure Array is the same number specified in constructor
                t1.setWidths(new int[]{10, 90});

                // t1col1
                PdfPCell t1col1 = new PdfPCell();
                t1col1.setBorder(PdfPCell.NO_BORDER);
                Resource resource = resourceLoader.getResource("classpath:report_image/logo_kemenkeu.jpg");
                Image img = Image.getInstance(resource.getURL());
                img.scalePercent(25, 25);
                img.setAlignment(Element.ALIGN_CENTER);
                t1col1.addElement(img);
                //t1col2
                PdfPCell t1col2 = new PdfPCell();
                t1col2.setBorder(PdfPCell.NO_BORDER);
                Paragraph t1para2 = new Paragraph(12);
                Paragraph t1para21 = new Paragraph(12);
                Paragraph t1para22 = new Paragraph(12);
                Paragraph t1para23 = new Paragraph(12);
                Paragraph t1para24 = new Paragraph(12);
                t1para2.setFont(font3);
                t1para24.setFont(font3);
                t1para21.setFont(font4);
                t1para23.setFont(font4);
                t1para22.setFont(font5);

                t1para2.setAlignment(Element.ALIGN_CENTER);
                t1para21.setAlignment(Element.ALIGN_CENTER);
                t1para22.setAlignment(Element.ALIGN_CENTER);
                t1para23.setAlignment(Element.ALIGN_CENTER);
                t1para24.setAlignment(Element.ALIGN_CENTER);
                //p1.setMultipliedLeading(1.5f);
                t1para21.add("KEMENTERIAN KEUANGAN REPUBLIK INDONESIA");
                t1para21.add(Chunk.NEWLINE);
                t1para2.add("DIREKTORAT JENDERAL PAJAK");
                t1para2.add(Chunk.NEWLINE);
                t1para23.add(Objects.nonNull(lim.getNamaKanwil()) ? lim.getNamaKanwil().toUpperCase() : "-");
                t1para23.add(Chunk.NEWLINE);
                t1para24.add(Objects.nonNull(lim.getNamaKpp()) ? "KPP " + lim.getNamaKpp().toUpperCase() : "-");
                t1para24.add(Chunk.NEWLINE);
                t1para22.add(Objects.nonNull(kantor.getAlamatBaris1()) ? kantor.getAlamatBaris1().toUpperCase() : "-");
                t1para22.add(Chunk.NEWLINE);
                t1para22.add(Objects.nonNull(kantor.getAlamatBaris2()) ? kantor.getAlamatBaris2() + " SITUS www.pajak.go.id" : "- SITUS www.pajak.go.id");
                t1para22.add(Chunk.NEWLINE);
                t1para22.add("LAYANAN INFORMASI DAN KELUHAN KRING PAJAK (021) 1-500-200");
                t1para22.add(Chunk.NEWLINE);
                t1para22.add("EMAIL: pengaduan@pajak.go.id");
                t1col2.addElement(t1para21);
                t1col2.addElement(t1para2);
                t1col2.addElement(t1para23);
                t1col2.addElement(t1para24);
                t1col2.addElement(t1para22);

                for (PdfPCell cell : Arrays.asList(t1col1, t1col2)) {
                    t1.addCell(cell);
                }

                // Table 2
                PdfPTable t2 = new PdfPTable(2);
                t2.setWidthPercentage(95);
                // Set Each Column Width - Make Sure Array is the same number specified in constructor
                t2.setWidths(new int[]{0, 100});
                //t2col1
                PdfPCell t2col1 = new PdfPCell(new Phrase(" ", font));
                t2col1.setBorder(PdfPCell.NO_BORDER);
                //t2col2
                PdfPCell t2col2 = new PdfPCell();
                t2col2.setBorder(PdfPCell.NO_BORDER);
                Paragraph t2para1 = new Paragraph();
                t2para1.setFont(font1);
                t2para1.setAlignment(Element.ALIGN_CENTER);
                //p1.setMultipliedLeading(1.5f);
                t2para1.add("SURAT KETERANGAN TERDAFTAR OBJEK PAJAK");
                t2para1.add(Chunk.NEWLINE);
                t2para1.add("PAJAK BUMI DAN BANGUNAN (PBB)");
                t2para1.add(Chunk.NEWLINE);
                t2col2.addElement(t2para1);
                //t2col3
                PdfPCell t2col3 = new PdfPCell(new Phrase(" ", font));
                t2col3.setBorder(PdfPCell.NO_BORDER);
                //t2col4
                PdfPCell t2col4 = new PdfPCell();
                t2col4.setBorder(PdfPCell.NO_BORDER);
                Paragraph t2para2 = new Paragraph();
                t2para2.setFont(font);
                t2para2.setAlignment(Element.ALIGN_CENTER);
                //p1.setMultipliedLeading(1.5f);
                t2para2.add(Objects.nonNull(lim.getNoSkt()) ? "Nomor : " + lim.getNoSkt().toUpperCase() : "Nomor : -");
                t2para2.add(Chunk.NEWLINE);
                t2col2.addElement(t2para2);

                for (PdfPCell pCell : Arrays.asList(t2col1, t2col2, t2col3, t2col4)) {
                    t2.addCell(pCell);
                }

                //table 3
                PdfPTable t3 = new PdfPTable(1);
                t3.setWidthPercentage(95);
                //t3col1
                PdfPCell t3col1 = new PdfPCell();
                t3col1.setBorder(PdfPCell.NO_BORDER);
                Paragraph t3para1 = new Paragraph(12);
                t3para1.setFont(font);
                t3para1.setAlignment(Element.ALIGN_JUSTIFIED);
                t3para1.setFirstLineIndent(40);
                //p1.setMultipliedLeading(1.5f);
                t3para1.add("Sesuai dengan Pasal 9 ayat (3) Undang-Undang Nomor 12 Tahun 1985 tentang Pajak Bumi dan Bangunan dan perubahannya serta Pasal 29 Peraturan Menteri Keuangan Nomor 48/PMK.03/2021 tentang Tata Cara Pendaftaran, Pelaporan, dan Pendataan Objek Pajak Bumi dan Bangunan, dengan ini diterangkan bahwa :");
                t3para1.add(Chunk.NEWLINE);
                t3col1.addElement(t3para1);

                t3.addCell(t3col1);

                //table 4
                PdfPTable t4 = new PdfPTable(5);
                t4.setWidthPercentage(95);
                // Set Each Column Width - Make Sure Array is the same number specified in constructor
                t4.setWidths(new int[]{5, 5, 25, 5, 60});

                //1. Nama Objek Pajak
                PdfPCell t4col1 = new PdfPCell();
                t4col1.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para1 = new Paragraph(12);
                t4para1.setFont(font);
                t4para1.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para1.add("1.");
                t4col1.addElement(t4para1);

                PdfPCell t4col2 = new PdfPCell();
                t4col2.setBorder(PdfPCell.NO_BORDER);
                t4col2.setColspan(2);
                Paragraph t4para2 = new Paragraph(12);
                t4para2.setFont(font);
                t4para2.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para2.add("Nama Objek Pajak");
                t4col2.addElement(t4para2);

                PdfPCell t4col3 = new PdfPCell();
                t4col3.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para3 = new Paragraph(12);
                t4para3.setFont(font);
                t4para3.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para3.add(":");
                t4col3.addElement(t4para3);

                PdfPCell t4col4 = new PdfPCell();
                t4col4.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para4 = new Paragraph(12);
                t4para4.setFont(font);
                t4para4.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para4.add(Objects.nonNull(lim.getNamaObjekPajak()) ? lim.getNamaObjekPajak().toUpperCase() : "-");
                t4col4.addElement(t4para4);

                for (PdfPCell pCell : Arrays.asList(t4col1, t4col2, t4col3, t4col4)) {
                    t4.addCell(pCell);
                }

                //2. NOP
                PdfPCell t4col5 = new PdfPCell();
                t4col5.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para5 = new Paragraph(12);
                t4para5.setFont(font);
                t4para5.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para5.add("2.");
                t4col5.addElement(t4para5);

                PdfPCell t4col6 = new PdfPCell();
                t4col6.setBorder(PdfPCell.NO_BORDER);
                t4col6.setColspan(2);
                Paragraph t4para6 = new Paragraph(12);
                t4para6.setFont(font);
                t4para6.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para6.add("NOP");
                t4col6.addElement(t4para6);

                PdfPCell t4col7 = new PdfPCell();
                t4col7.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para7 = new Paragraph(12);
                t4para7.setFont(font);
                t4para7.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para7.add(":");
                t4col7.addElement(t4para7);

                PdfPCell t4col8 = new PdfPCell();
                t4col8.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para8 = new Paragraph(12);
                t4para8.setFont(font);
                t4para8.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
//                t4para8.add(String.format("99.99.999.999.999-9999.9", lim.getNop()));
                t4para8.add(Objects.nonNull(lim.getNop()) ? lim.getNop().replaceAll("^(\\d{2})(\\d{2})(\\d{3})(\\d{3})(\\d{3})(\\d{4})(\\d{1})$", "$1.$2.$3.$4.$5-$6.$7") : "-");
                t4col8.addElement(t4para8);

                for (PdfPCell pCell : Arrays.asList(t4col5, t4col6, t4col7, t4col8)) {
                    t4.addCell(pCell);
                }

                //3. Klasifikasi Objek Pajak
                PdfPCell t4col9 = new PdfPCell();
                t4col9.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para9 = new Paragraph(12);
                t4para9.setFont(font);
                t4para9.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para9.add("3.");
                t4col9.addElement(t4para9);

                PdfPCell t4col10 = new PdfPCell();
                t4col10.setBorder(PdfPCell.NO_BORDER);
                t4col10.setColspan(2);
                Paragraph t4para10 = new Paragraph(12);
                t4para10.setFont(font);
                t4para10.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para10.add("Klasifikasi Objek Pajak");
                t4col10.addElement(t4para10);

                PdfPCell t4col11 = new PdfPCell();
                t4col11.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para11 = new Paragraph(12);
                t4para11.setFont(font);
                t4para11.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para11.add(":");
                t4col11.addElement(t4para11);

                PdfPCell t4col12 = new PdfPCell();
                t4col12.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para12 = new Paragraph(12);
                t4para12.setFont(font);
                t4para12.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para12.add(Objects.nonNull(lim.getNamaSektor()) ? lim.getNamaSektor().toUpperCase() : "-");
                t4col12.addElement(t4para12);

                for (PdfPCell pCell : Arrays.asList(t4col9, t4col10, t4col11, t4col12)) {
                    t4.addCell(pCell);
                }

                //4. Subsektor
                PdfPCell t4col13 = new PdfPCell();
                t4col13.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para13 = new Paragraph(12);
                t4para13.setFont(font);
                t4para13.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para13.add("4.");
                t4col13.addElement(t4para13);

                PdfPCell t4col14 = new PdfPCell();
                t4col14.setBorder(PdfPCell.NO_BORDER);
                t4col14.setColspan(2);
                Paragraph t4para14 = new Paragraph(12);
                t4para14.setFont(font);
                t4para14.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para14.add("Subsektor");
                t4col14.addElement(t4para14);

                PdfPCell t4col15 = new PdfPCell();
                t4col15.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para15 = new Paragraph(12);
                t4para15.setFont(font);
                t4para15.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para15.add(":");
                t4col15.addElement(t4para15);

                PdfPCell t4col16 = new PdfPCell();
                t4col16.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para16 = new Paragraph(12);
                t4para16.setFont(font);
                t4para16.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para16.add(Objects.nonNull(lim.getNamaSubSektor()) ? lim.getNamaSubSektor().toUpperCase() : "-");
                t4col16.addElement(t4para16);

                for (PdfPCell pCell : Arrays.asList(t4col13, t4col14, t4col15, t4col16)) {
                    t4.addCell(pCell);
                }

                //5. Lokasi Objek Pajak
                PdfPCell t4col17 = new PdfPCell();
                t4col17.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para17 = new Paragraph(12);
                t4para17.setFont(font);
                t4para17.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para17.add("5.");
                t4col17.addElement(t4para17);

                PdfPCell t4col18 = new PdfPCell();
                t4col18.setBorder(PdfPCell.NO_BORDER);
                t4col18.setColspan(2);
                Paragraph t4para18 = new Paragraph(12);
                t4para18.setFont(font);
                t4para18.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para18.add("Lokasi Objek Pajak");
                t4col18.addElement(t4para18);

                PdfPCell t4col19 = new PdfPCell();
                t4col19.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para19 = new Paragraph(12);
                t4para19.setFont(font);
                t4para19.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para19.add(":");
                t4col19.addElement(t4para19);

                PdfPCell t4col20 = new PdfPCell();
                t4col20.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para20 = new Paragraph(12);
                t4para20.setFont(font);
                t4para20.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para20.add(" ");
                t4col20.addElement(t4para20);

                for (PdfPCell pCell : Arrays.asList(t4col17, t4col18, t4col19, t4col20)) {
                    t4.addCell(pCell);
                }

                //5. Lokasi Objek Pajak set detail (a. Jalan)
                PdfPCell t4col21 = new PdfPCell();
                t4col21.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para21 = new Paragraph(12);
                t4para21.setFont(font);
                t4para21.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para21.add(" ");
                t4col21.addElement(t4para21);

                PdfPCell t4col22 = new PdfPCell();
                t4col22.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para22 = new Paragraph(12);
                t4para22.setFont(font);
                t4para22.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para22.add("a. ");
                t4col22.addElement(t4para22);

                PdfPCell t4col23 = new PdfPCell();
                t4col23.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para23 = new Paragraph(12);
                t4para23.setFont(font);
                t4para23.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para23.add("Jalan ");
                t4col23.addElement(t4para23);

                PdfPCell t4col24 = new PdfPCell();
                t4col24.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para24 = new Paragraph(12);
                t4para24.setFont(font);
                t4para24.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para24.add(":");
                t4col24.addElement(t4para24);

                PdfPCell t4col25 = new PdfPCell();
                t4col25.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para25 = new Paragraph(12);
                t4para25.setFont(font);
                t4para25.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para25.add(Objects.nonNull(lim.getLokNamaJalan()) ? lim.getLokNamaJalan().toUpperCase() : "-");
                t4col25.addElement(t4para25);

                for (PdfPCell pCell : Arrays.asList(t4col21, t4col22, t4col23, t4col24, t4col25)) {
                    t4.addCell(pCell);
                }

                //5. Lokasi Objek Pajak set detail (b. Kelurahan)
                PdfPCell t4col26 = new PdfPCell();
                t4col26.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para26 = new Paragraph(12);
                t4para26.setFont(font);
                t4para26.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para26.add(" ");
                t4col26.addElement(t4para26);

                PdfPCell t4col27 = new PdfPCell();
                t4col27.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para27 = new Paragraph(12);
                t4para27.setFont(font);
                t4para27.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para27.add("b. ");
                t4col27.addElement(t4para27);

                PdfPCell t4col28 = new PdfPCell();
                t4col28.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para28 = new Paragraph(12);
                t4para28.setFont(font);
                t4para28.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para28.add("Kelurahan/Desa ");
                t4col28.addElement(t4para28);

                PdfPCell t4col29 = new PdfPCell();
                t4col29.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para29 = new Paragraph(12);
                t4para29.setFont(font);
                t4para29.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para29.add(":");
                t4col29.addElement(t4para29);

                PdfPCell t4col30 = new PdfPCell();
                t4col30.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para30 = new Paragraph(12);
                t4para30.setFont(font);
                t4para30.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para30.add(Objects.nonNull(lim.getLokDesa()) ? lim.getLokDesa().toUpperCase() : "-");
                t4col30.addElement(t4para30);

                for (PdfPCell pCell : Arrays.asList(t4col26, t4col27, t4col28, t4col29, t4col30)) {
                    t4.addCell(pCell);
                }

                //5. Lokasi Objek Pajak set detail (c. Kecamatan)
                PdfPCell t4col31 = new PdfPCell();
                t4col31.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para31 = new Paragraph(12);
                t4para31.setFont(font);
                t4para31.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para31.add(" ");
                t4col31.addElement(t4para31);

                PdfPCell t4col32 = new PdfPCell();
                t4col32.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para32 = new Paragraph(12);
                t4para32.setFont(font);
                t4para32.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para32.add("c. ");
                t4col32.addElement(t4para32);

                PdfPCell t4col33 = new PdfPCell();
                t4col33.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para33 = new Paragraph(12);
                t4para33.setFont(font);
                t4para33.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para33.add("Kecamatan ");
                t4col33.addElement(t4para33);

                PdfPCell t4col34 = new PdfPCell();
                t4col34.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para34 = new Paragraph(12);
                t4para34.setFont(font);
                t4para34.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para34.add(":");
                t4col34.addElement(t4para34);

                PdfPCell t4col35 = new PdfPCell();
                t4col35.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para35 = new Paragraph(12);
                t4para35.setFont(font);
                t4para35.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para35.add(Objects.nonNull(lim.getLokKecamatan()) ? lim.getLokKecamatan().toUpperCase() : "-");
                t4col35.addElement(t4para35);

                for (PdfPCell pCell : Arrays.asList(t4col31, t4col32, t4col33, t4col34, t4col35)) {
                    t4.addCell(pCell);
                }

                //5. Lokasi Objek Pajak set detail (d. Kota/Kabupaten)
                PdfPCell t4col36 = new PdfPCell();
                t4col36.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para36 = new Paragraph(12);
                t4para36.setFont(font);
                t4para36.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para36.add(" ");
                t4col36.addElement(t4para36);

                PdfPCell t4col37 = new PdfPCell();
                t4col37.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para37 = new Paragraph(12);
                t4para37.setFont(font);
                t4para37.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para37.add("d. ");
                t4col37.addElement(t4para37);

                PdfPCell t4col38 = new PdfPCell();
                t4col38.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para38 = new Paragraph(12);
                t4para38.setFont(font);
                t4para33.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para38.add("Kota/Kabupaten ");
                t4col38.addElement(t4para38);

                PdfPCell t4col39 = new PdfPCell();
                t4col39.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para39 = new Paragraph(12);
                t4para39.setFont(font);
                t4para39.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para39.add(":");
                t4col39.addElement(t4para39);

                PdfPCell t4col40 = new PdfPCell();
                t4col40.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para40 = new Paragraph(12);
                t4para40.setFont(font);
                t4para40.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para40.add(Objects.nonNull(lim.getLokKabupaten()) ? lim.getLokKabupaten().toUpperCase() : "-");
                t4col40.addElement(t4para40);

                for (PdfPCell pCell : Arrays.asList(t4col36, t4col37, t4col38, t4col39, t4col40)) {
                    t4.addCell(pCell);
                }

                //5. Lokasi Objek Pajak set detail (e. Provinsi)
                PdfPCell t4col41 = new PdfPCell();
                t4col41.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para41 = new Paragraph(12);
                t4para41.setFont(font);
                t4para41.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para41.add(" ");
                t4col41.addElement(t4para41);

                PdfPCell t4col42 = new PdfPCell();
                t4col42.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para42 = new Paragraph(12);
                t4para42.setFont(font);
                t4para42.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para42.add("e. ");
                t4col42.addElement(t4para42);

                PdfPCell t4col43 = new PdfPCell();
                t4col43.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para43 = new Paragraph(12);
                t4para43.setFont(font);
                t4para43.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para43.add("Provinsi ");
                t4col43.addElement(t4para43);

                PdfPCell t4col44 = new PdfPCell();
                t4col44.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para44 = new Paragraph(12);
                t4para44.setFont(font);
                t4para44.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para44.add(":");
                t4col44.addElement(t4para44);

                PdfPCell t4col45 = new PdfPCell();
                t4col45.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para45 = new Paragraph(12);
                t4para45.setFont(font);
                t4para45.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para45.add(Objects.nonNull(lim.getLokProvinsi()) ? lim.getLokProvinsi().toUpperCase() : "-");
                t4col45.addElement(t4para45);

                for (PdfPCell pCell : Arrays.asList(t4col41, t4col42, t4col43, t4col44, t4col45)) {
                    t4.addCell(pCell);
                }

                //5. Lokasi Objek Pajak set detail (f. Kode Pos)
                PdfPCell t4col46 = new PdfPCell();
                t4col46.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para46 = new Paragraph(12);
                t4para46.setFont(font);
                t4para46.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para46.add(" ");
                t4col46.addElement(t4para46);

                PdfPCell t4col47 = new PdfPCell();
                t4col47.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para47 = new Paragraph(12);
                t4para47.setFont(font);
                t4para47.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para47.add("f. ");
                t4col47.addElement(t4para47);

                PdfPCell t4col48 = new PdfPCell();
                t4col48.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para48 = new Paragraph(12);
                t4para48.setFont(font);
                t4para48.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para48.add("Kode Pos ");
                t4col48.addElement(t4para48);

                PdfPCell t4col49 = new PdfPCell();
                t4col49.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para49 = new Paragraph(12);
                t4para49.setFont(font);
                t4para49.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para49.add(":");
                t4col49.addElement(t4para49);

                PdfPCell t4col50 = new PdfPCell();
                t4col50.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para50 = new Paragraph(12);
                t4para50.setFont(font);
                t4para50.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para50.add(lim.getLokKodePos());
                t4col50.addElement(t4para50);

                for (PdfPCell pCell : Arrays.asList(t4col46, t4col47, t4col48, t4col49, t4col50)) {
                    t4.addCell(pCell);
                }

                //Wajib Pajak atas Objek Pajak tersebut sebagai berikut:
                PdfPCell t4col51 = new PdfPCell();
                t4col51.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para51 = new Paragraph(12);
                t4para51.setFont(font);
                t4para51.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para51.add(" ");
                t4col51.addElement(t4para51);

                PdfPCell t4col52 = new PdfPCell();
                t4col52.setBorder(PdfPCell.NO_BORDER);
                t4col52.setColspan(4);
                Paragraph t4para52 = new Paragraph(12);
                t4para52.setFont(font);
                t4para52.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para52.add("Wajib Pajak atas Objek Pajak tersebut sebagai berikut: ");
                t4col52.addElement(t4para52);

                for (PdfPCell pCell : Arrays.asList(t4col51, t4col52)) {
                    t4.addCell(pCell);
                }

                //1. Nama Wajib Pajak
                PdfPCell t4col53 = new PdfPCell();
                t4col53.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para53 = new Paragraph(12);
                t4para53.setFont(font);
                t4para53.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para53.add("1.");
                t4col53.addElement(t4para53);

                PdfPCell t4col54 = new PdfPCell();
                t4col54.setBorder(PdfPCell.NO_BORDER);
                t4col54.setColspan(2);
                Paragraph t4para54 = new Paragraph(12);
                t4para54.setFont(font);
                t4para54.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para54.add("Nama Wajib Pajak");
                t4col54.addElement(t4para54);

                PdfPCell t4col55 = new PdfPCell();
                t4col55.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para55 = new Paragraph(12);
                t4para55.setFont(font);
                t4para55.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para55.add(":");
                t4col55.addElement(t4para55);

                PdfPCell t4col56 = new PdfPCell();
                t4col56.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para56 = new Paragraph(12);
                t4para56.setFont(font);
                t4para56.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para56.add(Objects.nonNull(lim.getNamaWp()) ? lim.getNamaWp().toUpperCase() : "-");
                t4col56.addElement(t4para56);

                for (PdfPCell pCell : Arrays.asList(t4col53, t4col54, t4col55, t4col56)) {
                    t4.addCell(pCell);
                }

                //2. NPWP
                PdfPCell t4col57 = new PdfPCell();
                t4col57.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para57 = new Paragraph(12);
                t4para57.setFont(font);
                t4para57.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para57.add("2.");
                t4col57.addElement(t4para57);

                PdfPCell t4col58 = new PdfPCell();
                t4col58.setBorder(PdfPCell.NO_BORDER);
                t4col58.setColspan(2);
                Paragraph t4para58 = new Paragraph(12);
                t4para58.setFont(font);
                t4para58.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para58.add("NPWP");
                t4col58.addElement(t4para58);

                PdfPCell t4col59 = new PdfPCell();
                t4col59.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para59 = new Paragraph(12);
                t4para59.setFont(font);
                t4para59.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para59.add(":");
                t4col59.addElement(t4para59);

                PdfPCell t4col60 = new PdfPCell();
                t4col60.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para60 = new Paragraph(12);
                t4para60.setFont(font);
                t4para60.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para60.add(Objects.nonNull(lim.getNpwp()) ? lim.getNpwp().replaceAll("^(\\d{2})(\\d{3})(\\d{3})(\\d{1})(\\d{3})(\\d{3})$", "$1.$2.$3.$4-$5.$6") : "-");
                t4col60.addElement(t4para60);

                for (PdfPCell pCell : Arrays.asList(t4col57, t4col58, t4col59, t4col60)) {
                    t4.addCell(pCell);
                }

                //3. Alamat
                PdfPCell t4col61 = new PdfPCell();
                t4col61.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para61 = new Paragraph(12);
                t4para61.setFont(font);
                t4para61.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para61.add("3.");
                t4col61.addElement(t4para61);

                PdfPCell t4col62 = new PdfPCell();
                t4col62.setBorder(PdfPCell.NO_BORDER);
                t4col62.setColspan(2);
                Paragraph t4para62 = new Paragraph(12);
                t4para62.setFont(font);
                t4para62.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para62.add("Alamat");
                t4col62.addElement(t4para62);

                PdfPCell t4col63 = new PdfPCell();
                t4col63.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para63 = new Paragraph(12);
                t4para63.setFont(font);
                t4para63.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para63.add(":");
                t4col63.addElement(t4para63);

                PdfPCell t4col64 = new PdfPCell();
                t4col64.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para64 = new Paragraph(12);
                t4para64.setFont(font);
                t4para64.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para64.add(Objects.nonNull(lim.getAlamatWP()) ? lim.getAlamatWP().toUpperCase() : "-");
                t4col64.addElement(t4para64);

                for (PdfPCell pCell : Arrays.asList(t4col61, t4col62, t4col63, t4col64)) {
                    t4.addCell(pCell);
                }

                //4. Jenis
                PdfPCell t4col65 = new PdfPCell();
                t4col65.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para65 = new Paragraph(12);
                t4para65.setFont(font);
                t4para65.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para65.add("4.");
                t4col65.addElement(t4para65);

                PdfPCell t4col66 = new PdfPCell();
                t4col66.setBorder(PdfPCell.NO_BORDER);
                t4col66.setColspan(2);
                Paragraph t4para66 = new Paragraph(12);
                t4para66.setFont(font);
                t4para66.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para66.add("Jenis");
                t4col66.addElement(t4para66);

                PdfPCell t4col67 = new PdfPCell();
                t4col67.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para67 = new Paragraph(12);
                t4para67.setFont(font);
                t4para67.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para67.add(":");
                t4col67.addElement(t4para67);

                PdfPCell t4col68 = new PdfPCell();
                t4col68.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para68 = new Paragraph(12);
                t4para68.setFont(font);
                t4para68.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para68.add(Objects.nonNull(lim.getJenisWP()) ? lim.getJenisWP().equals("0") ? "BADAN" : "ORANG PRIBADI" : "-");
                t4col68.addElement(t4para68);

                for (PdfPCell pCell : Arrays.asList(t4col65, t4col66, t4col67, t4col68)) {
                    t4.addCell(pCell);
                }

                //5. E-mail
                PdfPCell t4col69 = new PdfPCell();
                t4col69.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para69 = new Paragraph(12);
                t4para69.setFont(font);
                t4para69.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para69.add("5.");
                t4col69.addElement(t4para69);

                PdfPCell t4col70 = new PdfPCell();
                t4col70.setBorder(PdfPCell.NO_BORDER);
                t4col70.setColspan(2);
                Paragraph t4para70 = new Paragraph(12);
                t4para70.setFont(font2);
                t4para70.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para70.add("E-mail");
                t4col70.addElement(t4para70);

                PdfPCell t4col71 = new PdfPCell();
                t4col71.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para71 = new Paragraph(12);
                t4para71.setFont(font);
                t4para71.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para71.add(":");
                t4col71.addElement(t4para71);

                PdfPCell t4col72 = new PdfPCell();
                t4col72.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para72 = new Paragraph(12);
                t4para72.setFont(font);
                t4para72.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para72.add(Objects.nonNull(lim.getEmail()) ? lim.getEmail().toUpperCase() : "-");
                t4col72.addElement(t4para72);

                for (PdfPCell pCell : Arrays.asList(t4col69, t4col70, t4col71, t4col72)) {
                    t4.addCell(pCell);
                }

                //6. Nomor Telepon/Handphone
                PdfPCell t4col73 = new PdfPCell();
                t4col73.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para73 = new Paragraph(12);
                t4para73.setFont(font);
                t4para73.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para73.add("6.");
                t4col73.addElement(t4para73);

                PdfPCell t4col74 = new PdfPCell();
                t4col74.setBorder(PdfPCell.NO_BORDER);
                t4col74.setColspan(2);
                Paragraph t4para74 = new Paragraph(12);
                t4para74.setFont(font);
                t4para74.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para74.add("Nomor Telepon / Handphone");
                t4col74.addElement(t4para74);

                PdfPCell t4col75 = new PdfPCell();
                t4col75.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para75 = new Paragraph(12);
                t4para75.setFont(font);
                t4para75.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para75.add(":");
                t4col75.addElement(t4para75);

                PdfPCell t4col76 = new PdfPCell();
                t4col76.setBorder(PdfPCell.NO_BORDER);
                Paragraph t4para76 = new Paragraph(12);
                t4para76.setFont(font);
                t4para76.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para76.add(lim.getNoTelp());
                t4col76.addElement(t4para76);

                for (PdfPCell pCell : Arrays.asList(t4col73, t4col74, t4col75, t4col76)) {
                    t4.addCell(pCell);
                }

                PdfPCell t4col77 = new PdfPCell();
                t4col77.setBorder(PdfPCell.NO_BORDER);
                t4col77.setColspan(5);
                Paragraph t4para77 = new Paragraph(12);
                t4para77.setFont(font);
                t4para77.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                t4para77.add("telah terdaftar pada administrasi Direktorat Jenderal Pajak sebelum berlakunya Peraturan Menteri Keuangan Nomor 48/PMK.03/2021 tentang Tata Cara Pendaftaran, Pelaporan, dan Pendataan Objek Pajak Pajak Bumi dan Bangunan dan memiliki kewajiban melakukan Pelaporan SPOP dan Pembayaran PBB, atas objek pajak PBB sesuai dengan Undang-Undang PBB.");
                t4para77.setSpacingAfter(30);
                t4col77.addElement(t4para77);

                for (PdfPCell pCell : Arrays.asList(t4col77)) {
                    t4.addCell(pCell);
                }

                // Table 2
                PdfPTable t5 = new PdfPTable(2);
                t5.setWidthPercentage(100);
                // Set Each Column Width - Make Sure Array is the same number specified in constructor
                t5.setWidths(new int[]{55, 45});

                //t5col1
                PdfPCell t5col1 = new PdfPCell(new Phrase(" ", font));
                t5col1.setBorder(PdfPCell.NO_BORDER);
                Paragraph t5para11 = new Paragraph();
                t5para11.setFont(font);
                t5para11.setAlignment(Element.ALIGN_RIGHT);
                t5para11.add(Chunk.NEWLINE);
                t5para11.add(Chunk.NEWLINE);
                t5para11.add(Objects.nonNull(lim.getIsPjs()) ? lim.getIsPjs().equals("1") ? "Plh." : "" : "");
                t5col1.addElement(t5para11);
                //t5col2
                PdfPCell t5col2 = new PdfPCell();
                t5col2.setBorder(PdfPCell.NO_BORDER);
                Paragraph t5para1 = new Paragraph();
                t5para1.setFont(font);
                t5para1.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                String kotaTerbit = Objects.nonNull(lim.getTempatTtd()) ? lim.getTempatTtd().toLowerCase() : "";
                String kotaCapitalize = kotaTerbit.equals("") ? "" : new StringBuilder(kotaTerbit.substring(0, 1).toUpperCase()).append(kotaTerbit.substring(1)).toString();
                t5para1.add(kotaCapitalize + ", ");
                t5para1.add(Objects.nonNull(lim.getTanggalTtd()) ? lim.getTanggalTtd() : "-");
                t5para1.add(Chunk.NEWLINE);
                t5para1.add("a.n. Kepala Kantor");
                t5para1.add(Chunk.NEWLINE);
                t5para1.add("Kepala Seksi Pelayanan");
                t5para1.add(Chunk.NEWLINE);
                t5col2.addElement(t5para1);

                //t5col3
                PdfPCell t5col3 = new PdfPCell(new Phrase(" ", font));
                t5col3.setBorder(PdfPCell.NO_BORDER);

                //t5col4
                PdfPCell t5col4 = new PdfPCell();
                t5col4.setBorder(PdfPCell.NO_BORDER);
                Paragraph t5para4 = new Paragraph();
                t5para4.setFont(font);
                t5para4.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                if (lim.getStatus().equals("0") || lim.getStatus().equals("1")) {
                    t5para4.add("TTD");
                    t5para4.add(Chunk.NEWLINE);
                    t5para4.setSpacingAfter(25);
                    t5col4.addElement(t5para4);
                    /*img4.scalePercent(75, 75);
                    img4.setAlignment(Element.ALIGN_LEFT);
                    t5col4.addElement(img4);*/
                } else {
                    img4.scalePercent(175, 175);
                    img4.setAlignment(Element.ALIGN_LEFT);
                    t5col4.addElement(img4);
                }


                //t5col5
                PdfPCell t5col5 = new PdfPCell();
                t5col5.setBorder(PdfPCell.NO_BORDER);
                Paragraph t5para5 = new Paragraph();
                t5para5.setFont(font);
                t5para5.add(" ");
                t5para5.add(Chunk.NEWLINE);
                Resource resource3 = resourceLoader.getResource("classpath:report_image/logobssn.png");
                Image img3 = Image.getInstance(resource3.getURL());
                img3.scalePercent(10, 10);
                img3.setAlignment(Element.ALIGN_LEFT);
                t5col5.addElement(t5para5);
                t5col5.addElement(img3);
                //t5col6
                PdfPCell t5col6 = new PdfPCell();
                t5col6.setBorder(PdfPCell.NO_BORDER);
                Paragraph t5para6 = new Paragraph();
                t5para6.setFont(font);
                t5para6.setAlignment(Element.ALIGN_JUSTIFIED);
                //p1.setMultipliedLeading(1.5f);
                if (lim.getStatus().equals("0") || lim.getStatus().equals("1")) {
                    t5para6.add("NAMA PEJABAT");
                    t5para6.add(Chunk.NEWLINE);
//                    t5para6.add("NIP. 0000000000000000");
                    t5col6.addElement(t5para6);
                } else {
                    t5para6.add(Objects.nonNull(lim.getNamaTtd()) ? lim.getNamaTtd().toUpperCase() : "-");
                    t5para6.add(Chunk.NEWLINE);
//                    t5para6.add(Objects.nonNull(lim.getNipTtd()) ? "NIP. " + lim.getNipTtd() : "NIP. -");
                    t5col6.addElement(t5para6);
                }

                for (PdfPCell pCell : Arrays.asList(t5col1, t5col2, t5col3, t5col4, t5col5, t5col6)) {
                    t5.addCell(pCell);
                }

                //document add each data-table
                document.add(t1);
                //draw line
                document.add(new LineSeparator(0.5f, 100, BaseColor.BLACK, 0, -5));
                document.add(t2);
                document.add(t3);
                if (lim.getStatus().equals("0") || lim.getStatus().equals("1")) {
                    document.add(img2);
                }
                document.add(t4);
                document.add(t5);


                document.close();
            } else {
                return null;
            }


        } catch (Exception ex) {
            return null;
        }


        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public  byte[] getSignedSktReport(String idSkt, UserSikkaModel user) {
        try {
            ResponseModel rm = sktService.getSkt(idSkt, user);
            if (rm.getKodeResponse() == 1) {
                DtoSkt objSkt = (DtoSkt) rm.getObjResponse();
                if (Objects.nonNull(objSkt.getFileLocation())) {
                    return hcpService.getFileInHcp(objSkt.getFileLocation());
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] getSignedSktReportFromSigningAgent(String idSkt, UserSikkaModel user) {
       try {
           ResponseModel rm = sktService.getSkt(idSkt, user);
           if (rm.getKodeResponse() == 1) {
               DtoSkt objSkt = (DtoSkt) rm.getObjResponse();
               if (Objects.nonNull(objSkt.getSignedDocId())) {
                   byte[] signedPdf = signingService.getSignedDocument(objSkt.getSignedDocId());
                   return signedPdf;
               }else{
                   return null;
               }
           }else{
               return null;
           }

       }catch (Exception ex){
           ex.printStackTrace();
           return null;
       }

    }


//    private ResponseModel getDataDetail(String idSkt, String kdKanwil, String kdKpp) {
//
//        Mono<ResponseModel> result = regWebClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/skt")
//                        .queryParam("idSkt", idSkt)
//                        .queryParam("kdKpp", kdKpp)
//                        .queryParam("kdKanwil", kdKanwil)
//                        .build())
//                .header("Authorization", getAuthorizationHeader("admin", "admin123"))
//                .retrieve()
//                .bodyToMono(ResponseModel.class);
//
//        ResponseModel res = result.block();
//
//        if (res.getKodeResponse()==1){
//            ObjectMapper mapper = new ObjectMapper();
//            DtoSkt pg = mapper.convertValue(res.getObjResponse(), DtoSkt.class);
//
//            return new ResponseModelBuilder().success().withData(pg).build();
//
//        }else{
//            return new ResponseModelBuilder().failed().withMessage("Gagal").build();
//        }
//
//        /*try {
//            Mono<ResponseModel> result = regWebClient.get()
//                    .uri(uriBuilder -> uriBuilder
//                            .path("/api/lhpp")
//                            .queryParam("idlhpp", idlhpp)
//                            .queryParam("kdKpp", kdKpp)
//                            .queryParam("kdKanwil", kdKanwil)
//                            .build())
//                    .header("Authorization", getAuthorizationHeader("admin", "admin123"))
//                    .retrieve()
//                    .bodyToMono(ResponseModel.class);
//
//            return result.block();
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return new ResponseModelBuilder().failed().withMessage(ex.getMessage()).build();
//        }*/
//    }

    private String getAuthorizationHeader(String clientId, String clientSecret) {
        String creds = String.format("%s:%s", clientId, clientSecret);
        try {
            return "Basic " + new String(Base64.getEncoder().encode(creds.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Tidak bisa convert");
        }
    }
}
