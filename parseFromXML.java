package javahomework;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;


public class parseFromXML
{
    //SoapClient classı içerisinde bağlantı için gerekli olan parametreler bu bölümde
    //Tanımlanmıştır. Her bir parametrenin eşi SOAPClient içerisinde bulunmaktadır
    //ve kullanım alanlarına değinilmiştir.
    static String endpoint = "http://81.214.73.178/TahsilatService/TahsilatService.asmx";
    static String namespace = "http://tempuri.org/";
    static String parameter = "referansNo";
    private final String referenceNumber;
    //Bu bölümdeki değişkenler ise SoapClient bağlantısı yapılıp veri çekildikten sonra 
    //verinin işleneceği değişkenler olarak tanımlanabilir. referenceName sorgulama yapılan
    //kişinin adı, referanceStatus yapılan sorgu sonucunda işlemin yapılıp yapılamadığının
    //içinde tutulacağı ve kullanıcıyı bilgilendirmek üzere containerFrame'e gönderilmek
    //için hazırlandı, referenceData çekilen verinin parçalara ayrılıp containerFrame içerisindeki
    //tabloya gönderilmek üzere hazırlanacağı nesene olarak hazırlandı, tableColumns ise yapılan
    //işleme göre tablo isimlerini içinde bulunduracak bir dizi olarak hazırlandı, tahsilat
    //veya borç sorgulama işlemlerinde sütun isimleri farklı olacağı için yapılan sorgudan
    //dönen veriler sayesinde içerği hazırlanmaktadır.
    private String referenceName;
    private String referenceStatus;
    private Object[][] referenceData;
    private String[] tableColumns;

    public parseFromXML(String getReference)
    {
        //containerFrame içerisinden tetiklenirken textBox içerisine yazılan referans numarasının
        //aynı isimli class içerisinde kullanılabilmesi için constructor bloğünda global alanda
        //tanımlanan referanceNumber değşikeni ile eşlenmektedir.
        this.referenceNumber = getReference;
    }
    
    public Object[][] getReferenceData()
    {
        //getReferenceData metodu Parçalara ayrılan veri containerFrame içerisindeki tablo için return edilmesi için
        //kullanılır.
        return referenceData;
    }

    public String[] getTableColumns()
    {
        //getTableColumns metodu Sütun isimlerini containerFrame içerisindeki tablo için return edilmesi için kullnılır.
        return tableColumns;
    }

    public String getReferenceName()
    {
        //getReferenceName metodu containerFrame içerisindeki label içerisinde gösterilebilmesi için kullanılır.
        //Sorgulama yapılan referans numarasının sahibinin adı soyadını döndürür.
        return referenceName;
    }
     
    public String getReferenceStatus()
    {
        //getReferenceStatus metodu containerFrame içerisindeki label içerisinde gösterilebilmesi için kullanılır.
        //Sorgulama sonucu durumu yani sogrulamanın başarılı olup olmadığı bu metod sayesinde anlaşılır ve
        //containerFrame içerisinde gösterilir.
        return referenceStatus;
    }
    
        public void parsing(String operation)
    {
        //Bağlantı esnasında herhangi bir sorun yaşanırsa, bu yaşanan sorun programın çalışmasına engel olmasın
        //ve oluşan hata ekranda yazdırılabilsin diye oluşturulmuştur try-catch bloğu.
        try
        {
            //SOAP client classı client değişkenine tanımlanarak client değişkeni aracılığıyla soapclient içerisindeki callSoapWebService
            //metodu çalıştırılarak DocumentBuilder classından üretilmiş builder değişkeni içerisine DocumentBuilderFactory classının new
            //instance metodu ile document için yeni yer açılırken, newDocumentBuilder metodu ile yeni belge hazırlanıyor. Bu belge içerisine
            //parse metodu ile client sorgusu parçalanarak aktarılıyor. docGetDocumentElement metodu sayesinde normalizasyon işlemi yapılıyor.
            SOAPClient client = new SOAPClient(endpoint, namespace, operation, parameter, referenceNumber);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(client.callSoapWebService());
            doc.getDocumentElement().normalize();

            //SoapMessage doc içerisinde tutulmaktadır, doc içerisinde taglar ve onların içerisinde başka tag bulunmaktadır.
            //Message(Part(Envelope(Header(Head,Head),Body(XMLContent))))) şeklinde gelen verinin node'larına ulaşmak için docChieldNodes
            //kullanılır. XMLContent içerisindeki sorgulama yapılan client'ın adını tutan tag docChieldNodes'un 2. indisine karşılık gelir,
            //sorgulama sonucu dönen mesajını tutan tag 1. indisde, sogulama işleminde yapılan operasyon tagı ise 4. indisde yer almaktadır.
            //Bunlar aynı zamanda tag isimleridir, global alanda tanımlanan referenceName bu alanda tutacağı veriyi doc.getElementsByTagName sayesinde
            //alır. önceden indis numaralarına göre hazırlanan taglar bu metod içerisinde kullanılır. Bu sayede tagların içerisindeki veriye ulaşılabilir.
            //referenceName ve referenceStatus atamaları yapıldıktan sonra, yapılacak operasyonun verileri NodeList classı sayesinde nList değşkeni
            //içerisine aktarılır. BorcDetay veya TahsilatDetay tagları kendini tekrarlayan ve içlerinde tüm bilgileri barındıran taglar olduğu için
            //her biri düğüm olarak düşünülebilir, bu düğümler içerisindeki eleman verilerine ulaşmak için NodeList kullanılır. childNodes ise
            //BorcDetay veya TahsilatDetay tagları içerisinde bulunan tagları bünyesinde barındırır. Bu sayede tabloda oluşturulacak sütun isimlerine
            //erişilmiş olunur.
            NodeList docChieldNodes = doc.getChildNodes().item(0).getChildNodes().item(0).getChildNodes().item(0).getChildNodes().item(0).getChildNodes();
            String clientName = docChieldNodes.item(2).getNodeName();
            String mesaj = docChieldNodes.item(1).getNodeName();
            String clientOperation = docChieldNodes.item(4).getNodeName();
            referenceName = ((Element)doc.getElementsByTagName(clientName).item(0)).getTextContent();
            referenceStatus = ((Element)doc.getElementsByTagName(mesaj).item(0)).getTextContent();
            NodeList nList = doc.getElementsByTagName(clientOperation);
            NodeList childNodes = nList.item(0).getChildNodes().item(0).getChildNodes();

            //Verilerin referenceData içerisine aktarılması ve object'in oluşturulması için boyutları burada alınır.
            //nListLength içerisine kaç adet verinin geldiği bilgisi, chieldNodesLength içerisinde ise kaç adet sütununun olduğu bilgisi tutulur.
            int nListLength = nList.getLength();
            int childNodesLength = childNodes.getLength();
            
            //Veriler ve tablo sütunlarının gelen veri miktarına göre ayarlandığı bölüm burasıdır.
            referenceData = new Object[nListLength][childNodesLength];
            tableColumns = new String[childNodesLength];
            
            //Önce tablo isimleri bir döngü aracılığıyla tableColumns içerisine aktarılır.
            for(int i = 0; i < childNodesLength; i++)
                tableColumns[i] = childNodes.item(i).getNodeName();

            //Daha sonra da tablonun verileri referenceData içerisine aktarılır. Her bir eleman içerisindeki veriye o tag adına ait veri çekilerek
            //oluşturulur. element.getElementsByTagName(tagName).item(0).getTextContent() metodunda kullanılan tagName o elemanın chielNode item'ı
            //yani tag ismini belirtir, örnek vermek gerekirse BorcDetay içerisindeki BorcReferansNo buradaki tagName'dir. Bu tag'a ait olan veri
            //getTextContent metodu sayesinde çekilip referenceData'nın içerisindeki yerine yazılır. Bu işlem her bir node'un eleman olarak
            //atanması sayesinde yapılır, element burada farklı node'ları tutan ve içeriğine erişebilmeyi sağlayan bir araç görevi görür.
            for(int i = 0; i < nListLength; i++)
            {
                Element element = (Element) nList.item(i);
                for(int j = 0; j < childNodesLength; j++)
                {
                 String tagName = childNodes.item(j).getNodeName();
                 referenceData[i][j] = element.getElementsByTagName(tagName).item(0).getTextContent();
                }
            }
        }
        catch(Exception ex)
        {
            //Oluşan hata bu bölümde Exception classının printStackTrace metodu sayesinde ekrana yazdırılır.
            ex.printStackTrace();
        }
    }
}
