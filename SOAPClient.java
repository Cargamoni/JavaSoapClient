package javahomework;

import java.io.*;
import javax.xml.soap.*;

public class SOAPClient {
    //SOAPClient içerisinde kullanılacak ve parametreleri callSoapWebService içerisinde kullanılabilmesini sağlayan
    //SOAPClient classının Global alanında tanımlanan String değişkenler tanımlanmıştır
    //SOAP_ENDPOINT sunucuya bağlanmak için kullanılacak adresi içinde tutacaktır. SOAPClient constructor'ının ilk parametresidir
    //SOAP_ACTION sunucu üzerinde hangi işlem yapılacasa o parametreyi belirler ve adresin sonuna eklenen kısımdır,
    //bknz -> "http://81.214.73.178/TahsilatService/TahsilatService.asmx/TahsilatSorgu"; burada Tahsilat Sorgu ACTION bölümüdür.
    //TARGET_NAMESPACE bağlantı kurulacak sunucunun isim uzayı yani domain name server üzerindeki adı burada http://tempuri.org/ olarak belirlenmiştir.
    //OPERATION_NAME olan değişken yapılacak işlemi belirtir, gelen parametereye göre hangi işlem yapılacaksa o sayfada işlem yapılır.
    //PARAMETER_VALUE alınacak referans numarası bu değişken içerisinde tutulur ve sorgulama bu değişken aracılığıyla yapılır.
    String SOAP_ENDPOINT;
    String SOAP_ACTION;
    String TARGET_NAMESPACE;
    String OPERATION_NAME;
    String PARAMETER_NAME;
    String PARAMETER_VALUE;

    public SOAPClient(String endpoint, String namespace, String operation, String parameter, String value) {
        //SOAPClient classının Constructor bölümü, soapClient çağırılırken gerekli olan parametrelerin
        //Bu bölümde global alandaki değişkenlere eşleşmesi yapılmaktadır.
    	SOAP_ENDPOINT = endpoint;
    	SOAP_ACTION = namespace + operation;
    	TARGET_NAMESPACE = namespace;
    	OPERATION_NAME = operation;
    	PARAMETER_NAME = parameter;
    	PARAMETER_VALUE = value;
    }

    public InputStream callSoapWebService() {
        try {
            //callSoapWebSwervice tetiklendiğinde SOAPConnectionFactory.newInstance metodu class üzerinden
            //bir SOAPConnectionFactory tipli bir değişken içerisinden kullanılmak üzere aktarılıyor. 
            //Bağlantı yapılabilmek için ise SOAPConnection classı kullanılıyor. tanımlanan soapConnectionFactory 
            //değişkeni aracılığıyla SOAPConnection tipli soapConnection değişkeni içerisine bağlantı 
            //soapConnectionFactory.createConnection() metodu ile tanımlanıyor.
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Soap Bağlantısı için tanımlamalar yapıldıktan sonra sorgulama isteği ve sorgulama isteğinin yapılacağı
            //adres ve yapılacak request işlemi parametreleri ile soapConnection.call metodu tetikleniyor. createSoapRequest
            //metodunun döndürdüğü değer ve end point parametreleri ile tetiklenen metodun sonucu SOAPMessage deklarasyonu ile
            //Tanımlanan soapResponse değişkeni içerisine tanımlanıyor. Bu yanıt yani soapResponse daha sonra bir stream aracılığıyla
            //yani aşağıda tanımlanmış outputstream ile sucunuya veri gönderilirken input stream ile sunucudan veri alınmış olur.
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), SOAP_ENDPOINT);

            // Ekrana yazma yerine parseFromXML classı içerisinden parçalarına ayılarak Graphical User Interface aracılığıyla
            // tabloya yazdırılarak yapılmaktadır.
            //System.out.println("Response SOAP Message:");
            //soapResponse.writeTo(System.out);
            //System.out.println();
            
            //ByteArrayOutputStream classı sunucuya gönderilecek sorgu yolunu betimler, bu class aracılığıyla yukarıda yaptığımız
            //soapResponse sorgusu bu kanal aracılığıyla sunucuya gönderilerek, InputStream classı aracılığıyla da gönderilen 
            //kanal üzerinden gelen veriler InputStream classının tipindeki input değişkenine aktarılarak, veri gönderim alım
            //işlemi tamamlanmış olur.
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            soapResponse.writeTo(stream);
            InputStream input = new ByteArrayInputStream(stream.toByteArray());

            //Açılan soapConnection bağlantısı kapatılır ve bir alt satırda da sunucudan çekilen veriler kullanılmak üzere return edilir.
            soapConnection.close();
            return input;

        } catch (Exception e) {
            //Sunucuya bağlanılırken oluşan hata konsol üzerinden incelenmek üzere yazılır.
            System.err.println("\nError occurred while sending SOAP Request to Server: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public SOAPMessage createSOAPRequest() throws Exception {
        //createSOAPRequest metodu callSoapWebService içerisinden tetiklenen ve sorgunun yapılarak
        //soapMessage içerisine yazılarak değişikliklerin kaydedildiği metoddur. Bu metod içerisinden
        //Aynı zamanda createSoapEnvelope metodu da tetiklenerek verinin hangi parçalarının alınacağı
        //da belirlenir. MessageFactory classı ile gelecek mesajın tutulacağı yeni bölme açılarak
        //bu alanı tutacak messageFactory değişkeni içerisine tanımlanır. Daha sonra SOAPMessage
        //classının tipindeki soapMessage içerisine, tanımlanan messageFactory classının createMessage
        //metodu tetiklenerek yeni mesaj oluşturulur.
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        //SoapMessage'ın verileri içinde tutacağı Envelope kısmı bu metod sayesinde oluşturulur.
        //Gerekli bilglendirme metod içersinde verilmiştir.
        createSoapEnvelope(soapMessage);

        //SoapMEssage'ın içerisinde bulundurulan başlıklar alınarak soapHeader bölümüne bu addHeader metodu
        //Sayesinde yapılmaktadır. Envelope'un bir parçası olan SOAPHeader mesaja bu bölümde eklenir.
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", SOAP_ACTION);

        //SoapMessage içerisinde yapılan değişiklikler burada kaydedilir. Eklenen Envelope kısmı ve Headerlar
        //gönderilmek üzere hazır bir şekilde kayıtlıdır.
        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes 
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");
        */
        
        //Son olarak da oluşturulan XML dökümanına ait mesaj return edilir.
        return soapMessage;
    }

    public void createSoapEnvelope(SOAPMessage soapMessage) throws SOAPException {
        //createSoapEnvelope oluşturulacak mesajın ana hatları bu metod sayesinde oluşturulur.
        //resimde de görüldüğü üzere envelope içerisinde  SOAPHeader ve SOAPBody elemanlarını taşır.
        //Envelope'u içerisinde bulunduran SOAPPart öncelikle kendi adıyla aynı olan 
        //class aracılığıyla parametre olarak alınan soapMessage classının getSOAPPart metodu tetiklenerek
        //soapPart değşikeni içerisinde tutulur.
        String namespace = "namespace";
        SOAPPart soapPart = soapMessage.getSOAPPart();

        //Daha sonra alınan soapPart içerisindeki envelope verisine erişilmek için soapPart classının getEnvelope
        //Metodu tetiklenir ve envelope değişkeni içerisine aktarılır. Burada envelope içersine aktarılan veriler
        //üzerinde addNamespaceDeclaration metodu ile taglara namespace tanımlamaları yapılır.
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(namespace, TARGET_NAMESPACE);

        //Envelope bölümünün body kısmı burada hazırlanır. envelope.getBody metodu ile tetiklenerek SOAPBody classının
        //tüm özelliklerini taşıyan soapBody içinde tutulur. Elemanlarına ayrılarak soapBodyElem ve soapBodyElem1 ile 
        //yapılacak işlemler OPERATION_NAME ile belirlenir. soapBody.addChieldElement metodu sayesinde operasyon adına
        //göre veriler soapBodyElem içerisine aktarılır. soapBodyElem içerisindeki taglar ise soapBodyElem.addChieldElement
        //metodu ile parametre ismine göre soapBodyElem1 içerisinde tutulur. Tag içerisindeki taglara ulaşım şekli
        //bu şekildedir. En son olarak da soapBodyElem1.addTextNode metodu ile referans numarasına ait node eklenir.
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement(OPERATION_NAME, namespace);
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement(PARAMETER_NAME, namespace);
        soapBodyElem1.addTextNode(PARAMETER_VALUE);
    }
}
