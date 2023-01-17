import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;

public class MedicalServiceImplTest {

    @Mock
    PatientInfoRepository patientInfoRepository;
    @Mock
    SendAlertService sendAlertService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void test_check_blood_pressure_send_message() {
        Mockito.when(patientInfoRepository.getById("patient1"))
                .thenReturn(new PatientInfo("patient1", null, null, null,
                        new HealthInfo(null, new BloodPressure(120, 80))));

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        medicalService.checkBloodPressure("patient1", new BloodPressure(120, 70));

        Mockito.verify(sendAlertService, Mockito.only()).send(Mockito.anyString());
    }

    @Test
    void test_check_blood_pressure_no_send_message() {
        Mockito.when(patientInfoRepository.getById("patient1"))
                .thenReturn(new PatientInfo("patient1", null, null, null,
                        new HealthInfo(null, new BloodPressure(120, 80))));

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        medicalService.checkBloodPressure("patient1", new BloodPressure(120, 80));

        Mockito.verify(sendAlertService, Mockito.never()).send(Mockito.anyString());
    }

    @Test
    void test_check_temperature_send_messages_or_not_with_argumentCaptor() {
        Mockito.when(patientInfoRepository.getById("patient1"))
                .thenReturn(new PatientInfo("patient1", null, null, null,
                        new HealthInfo(BigDecimal.valueOf(36.6), null)));

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        medicalService.checkTemperature("patient1", BigDecimal.valueOf(34.0));
        medicalService.checkTemperature("patient1", BigDecimal.valueOf(38.0));

        Mockito.verify(sendAlertService, Mockito.only()).send(argumentCaptor.capture());

        Assertions.assertEquals("Warning, patient with id: patient1, need help", argumentCaptor.getValue());

    }
}
