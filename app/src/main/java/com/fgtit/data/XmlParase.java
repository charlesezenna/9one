package com.fgtit.data;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fgtit.utils.ExtApi;

public class XmlParase {

    public static String getNodeString(Element em, String name) {
        try {
            return em.getElementsByTagName(name).item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {

        }
        return "";
    }

    public static ArrayList<AdminItem> paraseAdminItem(InputStream inputStream) {
        ArrayList<AdminItem> list = new ArrayList<AdminItem>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(inputStream);
            Element root = doc.getDocumentElement();
            NodeList items = root.getElementsByTagName("AdminItem");
            for (int i = 0; i < items.getLength(); i++) {
                AdminItem li = new AdminItem();
                Element lin = (Element) items.item(i);
                li.username = getNodeString(lin, "username");
                li.password = getNodeString(lin, "password");
                li.fingerm = getNodeString(lin, "fingerm");
                li.fingers = getNodeString(lin, "fingers");

                li.usertype = Integer.valueOf(getNodeString(lin, "usertype"));

                li.realname = getNodeString(lin, "realname");
                li.idcardno = getNodeString(lin, "idcardno");
                li.photo = getNodeString(lin, "photo");
                li.phonemobile = getNodeString(lin, "phonemobile");
                li.phonefix = getNodeString(lin, "phonefix");
                li.email = getNodeString(lin, "email");
                li.address = getNodeString(lin, "address");

                list.add(li);
            }
            //inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<RecordItem> paraseRecordItemList(InputStream inputStream) {
        ArrayList<RecordItem> list = new ArrayList<RecordItem>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(inputStream);
            Element root = doc.getDocumentElement();
            NodeList items = root.getElementsByTagName("RecordItem");
            for (int i = 0; i < items.getLength(); i++) {
                RecordItem ri = new RecordItem();
                Element lin = (Element) items.item(i);
                ri.id = getNodeString(lin, "id");
                ri.name = getNodeString(lin, "name");
                ri.datetime = getNodeString(lin, "datetime");
                ri.lat = getNodeString(lin, "lat");
                ri.lng = getNodeString(lin, "lng");
                ri.worktype = getNodeString(lin, "worktype");
                ri.linetype = getNodeString(lin, "linetype");
                ri.depttype = getNodeString(lin, "depttype");
                ri.type = getNodeString(lin, "type");
                ri.matric_no = getNodeString(lin, "matricNo");
                ri.first_name = getNodeString(lin, "first_name");
                ri.last_name = getNodeString(lin, "last_name");
                ri.middle_name = getNodeString(lin, "middle_name");
                ri.dob = getNodeString(lin, "dob");
                ri.sex = getNodeString(lin, "sex");
                ri.marrital_status = getNodeString(lin, "marrital_status");
                ri.religion = getNodeString(lin, "religion");
                ri.card = getNodeString(lin, "card");
                ri.card_serial = getNodeString(lin, "card_serial");
                ri.email = getNodeString(lin, "email");
                ri.phone = getNodeString(lin, "phone");
                ri.address = getNodeString(lin, "address");
                ri.nationality = getNodeString(lin, "nationality");
                ri.state = getNodeString(lin, "state");
                ri.lga = getNodeString(lin, "lga");
                ri.nok_name = getNodeString(lin, "nok_name");
                ri.nok_phone = getNodeString(lin, "nok_phone");
                ri.nok_email = getNodeString(lin, "nok_email");
                ri.nok_address = getNodeString(lin, "nok_address");
                ri.home_town = getNodeString(lin, "home_town");
                ri.faculty = getNodeString(lin, "department");
                ri.department = getNodeString(lin, "department");
                ri.specialization = getNodeString(lin, "specialization");
                ri.level = getNodeString(lin, "level");
                ri.session = getNodeString(lin, "session");
                list.add(ri);
            }
            //inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //WorkItem
    public static ArrayList<WorkItem> paraseWorkItemList(Document doc) {
        ArrayList<WorkItem> list = new ArrayList<WorkItem>();
        //DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            //DocumentBuilder builder = factory.newDocumentBuilder();
            Element root = doc.getDocumentElement();
            NodeList items = root.getElementsByTagName("WorkItem");
            if (items.getLength() > 0) {
                for (int i = 0; i < items.getLength(); i++) {
                    WorkItem wi = new WorkItem();
                    Element lin = (Element) items.item(i);
                    wi.worktype = getNodeString(lin, "worktype");
                    wi.workname = getNodeString(lin, "workname");
                    list.add(wi);
                }
            }
            //inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<WorkItem> paraseWorkItemList(InputStream inputStream) {
        ArrayList<WorkItem> list = new ArrayList<WorkItem>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(inputStream);
            Element root = doc.getDocumentElement();
            NodeList items = root.getElementsByTagName("WorkItem");
            if (items.getLength() > 0) {
                for (int i = 0; i < items.getLength(); i++) {
                    WorkItem wi = new WorkItem();
                    Element lin = (Element) items.item(i);
                    wi.worktype = getNodeString(lin, "worktype");
                    wi.workname = getNodeString(lin, "workname");
                    list.add(wi);
                }
            }
            //inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //LineItem
    public static ArrayList<LineItem> paraseLineItemList(Document doc) {
        ArrayList<LineItem> list = new ArrayList<LineItem>();
        //DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            //DocumentBuilder builder = factory.newDocumentBuilder();
            Element root = doc.getDocumentElement();
            NodeList items = root.getElementsByTagName("LineItem");
            if (items.getLength() > 0) {
                for (int i = 0; i < items.getLength(); i++) {
                    LineItem li = new LineItem();
                    Element lin = (Element) items.item(i);
                    li.linetype = getNodeString(lin, "linetype");
                    li.linename = getNodeString(lin, "linename");
                    list.add(li);
                }
            }
            //inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<LineItem> paraseLineItemList(InputStream inputStream) {
        ArrayList<LineItem> list = new ArrayList<LineItem>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(inputStream);
            Element root = doc.getDocumentElement();
            NodeList items = root.getElementsByTagName("LineItem");
            if (items.getLength() > 0) {
                for (int i = 0; i < items.getLength(); i++) {
                    LineItem li = new LineItem();
                    Element lin = (Element) items.item(i);
                    li.linetype = getNodeString(lin, "linetype");
                    li.linename = getNodeString(lin, "linename");
                    list.add(li);
                }
            }
            //inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //DeptItem
    public static ArrayList<DeptItem> paraseDeptItemList(Document doc) {
        ArrayList<DeptItem> list = new ArrayList<DeptItem>();
        //DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            //DocumentBuilder builder = factory.newDocumentBuilder();
            Element root = doc.getDocumentElement();
            NodeList items = root.getElementsByTagName("DeptItem");
            if (items.getLength() > 0) {
                for (int i = 0; i < items.getLength(); i++) {
                    DeptItem di = new DeptItem();
                    Element lin = (Element) items.item(i);
                    di.depttype = getNodeString(lin, "depttype");
                    di.deptname = getNodeString(lin, "deptname");
                    list.add(di);
                }
            }
            //inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<DeptItem> paraseDeptItemList(InputStream inputStream) {
        ArrayList<DeptItem> list = new ArrayList<DeptItem>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(inputStream);
            Element root = doc.getDocumentElement();
            NodeList items = root.getElementsByTagName("DeptItem");
            if (items.getLength() > 0) {
                for (int i = 0; i < items.getLength(); i++) {
                    DeptItem di = new DeptItem();
                    Element lin = (Element) items.item(i);
                    di.depttype = getNodeString(lin, "depttype");
                    di.deptname = getNodeString(lin, "deptname");
                    list.add(di);
                }
            }
            //inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //UserItem
    public static ArrayList<UserItem> paraseUserItemList(Document doc, boolean bloadphoto) {
        ArrayList<UserItem> list = new ArrayList<UserItem>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Element root = doc.getDocumentElement();
            NodeList items = root.getElementsByTagName("UserItem");
            if (items.getLength() > 0) {
                for (int i = 0; i < items.getLength(); i++) {
                    UserItem ri = new UserItem();
                    Element lin = (Element) items.item(i);
                    ri.id = getNodeString(lin, "id");
                    ri.name = getNodeString(lin, "name");
                    //ri.worktype=Integer.valueOf(getNodeString(lin,"worktype"));
                    //ri.linetype=Integer.valueOf(getNodeString(lin,"linetype"));
                    //ri.depttype=Integer.valueOf(getNodeString(lin,"depttype"));
                    ri.worktype = getNodeString(lin, "worktype");
                    ri.linetype = getNodeString(lin, "linetype");
                    ri.depttype = getNodeString(lin, "depttype");
                    ri.type = Integer.valueOf(getNodeString(lin, "type"));
                    ri.gender = Integer.valueOf(getNodeString(lin, "gender"));
                    ri.statu = Integer.valueOf(getNodeString(lin, "statu"));
                    ri.enroldate = getNodeString(lin, "enroldate");
                    ri.phone = getNodeString(lin, "phone");
                    ri.remark = getNodeString(lin, "remark");
                    ri.cardsn = getNodeString(lin, "cardsn");
                    ri.template1 = getNodeString(lin, "template1");
                    ri.template2 = getNodeString(lin, "template2");

                    ri.matric_no = getNodeString(lin, "matric_no");
                    ri.first_name = getNodeString(lin, "first_name");
                    ri.last_name = getNodeString(lin, "last_name");
                    ri.middle_name = getNodeString(lin, "middle_name");
                    ri.dob = getNodeString(lin, "dob");
                    ri.sex = getNodeString(lin, "sex");
                    ri.marrital_status = getNodeString(lin, "marrital_status");
                    ri.religion = getNodeString(lin, "religion");
                    ri.card = getNodeString(lin, "card");
                    ri.card_serial = getNodeString(lin, "card_serial");
                    ri.email = getNodeString(lin, "email");
                    ri.phone = getNodeString(lin, "phone");
                    ri.address = getNodeString(lin, "address");
                    ri.nationality = getNodeString(lin, "nationality");
                    ri.state = getNodeString(lin, "state");
                    ri.lga = getNodeString(lin, "lga");
                    ri.nok_name = getNodeString(lin, "nok_name");
                    ri.nok_phone = getNodeString(lin, "nok_phone");
                    ri.nok_email = getNodeString(lin, "nok_email");
                    ri.nok_address = getNodeString(lin, "nok_address");
                    ri.home_town = getNodeString(lin, "home_town");
                    ri.faculty = getNodeString(lin, "faculty");
                    ri.department = getNodeString(lin, "department");
                    ri.specialization = getNodeString(lin, "specialization");
                    ri.level = getNodeString(lin, "level");
                    ri.session = getNodeString(lin, "session");
                    try {
                        ri.isSyncWithBackend = Boolean.valueOf(getNodeString(lin, "isSyncWithBackend"));

                    } catch (Exception e) {

                    }
                    if (ri.template1.length() >= 512)
                        ri.bytes1 = ExtApi.Base64ToBytes(ri.template1);
                    if (ri.template2.length() >= 512)
                        ri.bytes2 = ExtApi.Base64ToBytes(ri.template2);

                    if (bloadphoto)
                        ri.photo = getNodeString(lin, "photo");
                    list.add(ri);
                }
            }
            //inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<UserItem> paraseUserItemList(InputStream inputStream) {
        ArrayList<UserItem> list = new ArrayList<UserItem>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(inputStream);
            Element root = doc.getDocumentElement();
            NodeList items = root.getElementsByTagName("UserItem");
            if (items.getLength() > 0) {
                for (int i = 0; i < items.getLength(); i++) {
                    UserItem ri = new UserItem();
                    Element lin = (Element) items.item(i);
                    ri.id = getNodeString(lin, "id");
                    ri.name = getNodeString(lin, "name");
                    //ri.worktype=Integer.valueOf(getNodeString(lin,"worktype"));
                    //ri.linetype=Integer.valueOf(getNodeString(lin,"linetype"));
                    //ri.depttype=Integer.valueOf(getNodeString(lin,"depttype"));
                    ri.worktype = getNodeString(lin, "worktype");
                    ri.linetype = getNodeString(lin, "linetype");
                    ri.depttype = getNodeString(lin, "depttype");
                    ri.type = Integer.valueOf(getNodeString(lin, "type"));
                    ri.gender = Integer.valueOf(getNodeString(lin, "gender"));
                    ri.statu = Integer.valueOf(getNodeString(lin, "statu"));
                    ri.enroldate = getNodeString(lin, "enroldate");
                    ri.phone = getNodeString(lin, "phone");
                    ri.remark = getNodeString(lin, "remark");
                    ri.cardsn = getNodeString(lin, "cardsn");
                    ri.template1 = getNodeString(lin, "template1");
                    ri.template2 = getNodeString(lin, "template2");
                    ri.photo = getNodeString(lin, "photo");

                    ri.matric_no = getNodeString(lin, "matricNo");
                    ri.first_name = getNodeString(lin, "first_name");
                    ri.last_name = getNodeString(lin, "last_name");
                    ri.middle_name = getNodeString(lin, "maddle_name");
                    ri.dob = getNodeString(lin, "dob");
                    ri.sex = getNodeString(lin, "sex");
                    ri.marrital_status = getNodeString(lin, "marrital_status");
                    ri.religion = getNodeString(lin, "religion");
                    ri.card = getNodeString(lin, "card");
                    ri.card_serial = getNodeString(lin, "card_serial");
                    ri.email = getNodeString(lin, "email");
                    ri.phone = getNodeString(lin, "phone");
                    ri.address = getNodeString(lin, "address");
                    ri.nationality = getNodeString(lin, "nationality");
                    ri.state = getNodeString(lin, "state");
                    ri.lga = getNodeString(lin, "lga");
                    ri.nok_name = getNodeString(lin, "nok_name");
                    ri.nok_phone = getNodeString(lin, "nok_phone");
                    ri.nok_email = getNodeString(lin, "nok_email");
                    ri.nok_address = getNodeString(lin, "nok_address");
                    ri.home_town = getNodeString(lin, "home_town");
                    ri.faculty = getNodeString(lin, "department");
                    ri.department = getNodeString(lin, "department");
                    ri.specialization = getNodeString(lin, "specialization");
                    ri.level = getNodeString(lin, "level");
                    ri.session = getNodeString(lin, "session");

                    list.add(ri);
                }
            }
            //inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Document UserItemListToXml(List<UserItem> list) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = dbf.newDocumentBuilder();
        } catch (Exception e) {
        }

        Document doc = builder.newDocument();
        Element root = doc.createElement("UserItem");
        doc.appendChild(root); //

        for (int i = 0; i < list.size(); i++) {
            Element le = doc.createElement("UserItem");
            root.appendChild(le);

            Element id = doc.createElement("id");
            le.appendChild(id);
            id.appendChild(doc.createTextNode(list.get(i).id));

            Element name = doc.createElement("name");
            le.appendChild(name);
            name.appendChild(doc.createTextNode(list.get(i).name));
            //
            Element worktype = doc.createElement("worktype");
            le.appendChild(worktype);
            worktype.appendChild(doc.createTextNode(String.valueOf(list.get(i).worktype)));

            Element linetype = doc.createElement("linetype");
            le.appendChild(linetype);
            linetype.appendChild(doc.createTextNode(String.valueOf(list.get(i).linetype)));

            Element depttype = doc.createElement("depttype");
            le.appendChild(depttype);
            depttype.appendChild(doc.createTextNode(String.valueOf(list.get(i).depttype)));

            Element type = doc.createElement("type");
            le.appendChild(type);
            type.appendChild(doc.createTextNode(String.valueOf(list.get(i).type)));

            Element gender = doc.createElement("gender");
            le.appendChild(gender);
            gender.appendChild(doc.createTextNode(String.valueOf(list.get(i).gender)));

            Element statu = doc.createElement("statu");
            le.appendChild(statu);
            statu.appendChild(doc.createTextNode(String.valueOf(list.get(i).statu)));

            Element phone = doc.createElement("phone");
            le.appendChild(phone);
            phone.appendChild(doc.createTextNode(list.get(i).phone));

            Element enroldate = doc.createElement("enroldate");
            le.appendChild(enroldate);
            enroldate.appendChild(doc.createTextNode(list.get(i).enroldate));

            Element remark = doc.createElement("remark");
            le.appendChild(remark);
            remark.appendChild(doc.createTextNode(list.get(i).remark));

            Element cardsn = doc.createElement("cardsn");
            le.appendChild(cardsn);
            cardsn.appendChild(doc.createTextNode(list.get(i).cardsn));

            Element template1 = doc.createElement("template1");
            le.appendChild(template1);
            template1.appendChild(doc.createTextNode(list.get(i).template1));

            Element template2 = doc.createElement("template2");
            le.appendChild(template2);
            template2.appendChild(doc.createTextNode(list.get(i).template2));

            Element photo = doc.createElement("photo");
            le.appendChild(photo);
            photo.appendChild(doc.createTextNode(list.get(i).photo));

            Element matric_no = doc.createElement("matric_no");
            le.appendChild(matric_no);
            matric_no.appendChild(doc.createTextNode(list.get(i).matric_no));

            Element first_name = doc.createElement("first_name");
            le.appendChild(first_name);
            first_name.appendChild(doc.createTextNode(list.get(i).first_name));

            Element last_name = doc.createElement("last_name");
            le.appendChild(last_name);
            last_name.appendChild(doc.createTextNode(list.get(i).last_name));

            Element middle_name = doc.createElement("middle_name");
            le.appendChild(middle_name);
            middle_name.appendChild(doc.createTextNode(list.get(i).middle_name));

            Element madien_name = doc.createElement("madien_name");
            le.appendChild(madien_name);
            madien_name.appendChild(doc.createTextNode(list.get(i).madien_name));

            Element dob = doc.createElement("dob");
            le.appendChild(dob);
            dob.appendChild(doc.createTextNode(list.get(i).dob));

            Element marrital_status = doc.createElement("marrital_status");
            le.appendChild(marrital_status);
            marrital_status.appendChild(doc.createTextNode(list.get(i).marrital_status));

            Element religion = doc.createElement("religion");
            le.appendChild(religion);
            religion.appendChild(doc.createTextNode(list.get(i).religion));

            Element card = doc.createElement("card");
            le.appendChild(card);
            card.appendChild(doc.createTextNode(list.get(i).card));

            Element card_serial = doc.createElement("card_serial");
            le.appendChild(card_serial);
            card_serial.appendChild(doc.createTextNode(list.get(i).card_serial));

            Element email = doc.createElement("email");
            le.appendChild(email);
            email.appendChild(doc.createTextNode(list.get(i).email));

            Element address = doc.createElement("address");
            le.appendChild(address);
            address.appendChild(doc.createTextNode(list.get(i).address));

            Element nationality = doc.createElement("nationality");
            le.appendChild(nationality);
            nationality.appendChild(doc.createTextNode(list.get(i).nationality));

            Element state = doc.createElement("state");
            le.appendChild(state);
            state.appendChild(doc.createTextNode(list.get(i).state));

            Element lga = doc.createElement("lga");
            le.appendChild(lga);
            lga.appendChild(doc.createTextNode(list.get(i).lga));

            Element nok_name = doc.createElement("nok_name");
            le.appendChild(nok_name);
            nok_name.appendChild(doc.createTextNode(list.get(i).nok_name));

            Element sex = doc.createElement("sex");
            le.appendChild(sex);
            sex.appendChild(doc.createTextNode(list.get(i).sex));

            Element nok_email = doc.createElement("nok_email");
            le.appendChild(nok_email);
            nok_email.appendChild(doc.createTextNode(list.get(i).nok_email));

            Element nok_phone = doc.createElement("nok_phone");
            le.appendChild(nok_phone);
            nok_phone.appendChild(doc.createTextNode(list.get(i).nok_phone));

            Element nok_address = doc.createElement("nok_address");
            le.appendChild(nok_address);
            nok_address.appendChild(doc.createTextNode(list.get(i).nok_address));

            Element home_town = doc.createElement("home_town");
            le.appendChild(home_town);
            home_town.appendChild(doc.createTextNode(list.get(i).home_town));

            Element faculty = doc.createElement("faculty");
            le.appendChild(faculty);
            faculty.appendChild(doc.createTextNode(list.get(i).faculty));

            Element department = doc.createElement("department");
            le.appendChild(department);
            department.appendChild(doc.createTextNode(list.get(i).department));

            Element specialization = doc.createElement("specialization");
            le.appendChild(specialization);
            specialization.appendChild(doc.createTextNode(list.get(i).specialization));

            Element level = doc.createElement("level");
            le.appendChild(level);
            level.appendChild(doc.createTextNode(list.get(i).level));

            Element session = doc.createElement("session");
            le.appendChild(session);
            session.appendChild(doc.createTextNode(list.get(i).session));

            Element isSyncWithBackend = doc.createElement("isSyncWithBackend");
            le.appendChild(isSyncWithBackend);
            isSyncWithBackend.appendChild(doc.createTextNode(String.valueOf(list.get(i).isSyncWithBackend)));
        }

        return doc;
    }

    public static Document UserItemToXml(UserItem ui) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = dbf.newDocumentBuilder();
        } catch (Exception e) {
        }

        Document doc = builder.newDocument();
        Element root = doc.createElement("UserItem");
        doc.appendChild(root); //


        Element le = doc.createElement("UserItem");
        root.appendChild(le);

        Element id = doc.createElement("id");
        le.appendChild(id);
        id.appendChild(doc.createTextNode(ui.id));

        Element name = doc.createElement("name");
        le.appendChild(name);
        name.appendChild(doc.createTextNode(ui.name));
        //
        Element worktype = doc.createElement("worktype");
        le.appendChild(worktype);
        worktype.appendChild(doc.createTextNode(String.valueOf(ui.worktype)));

        Element linetype = doc.createElement("linetype");
        le.appendChild(linetype);
        linetype.appendChild(doc.createTextNode(String.valueOf(ui.linetype)));

        Element depttype = doc.createElement("depttype");
        le.appendChild(depttype);
        depttype.appendChild(doc.createTextNode(String.valueOf(ui.depttype)));

        Element type = doc.createElement("type");
        le.appendChild(type);
        type.appendChild(doc.createTextNode(String.valueOf(ui.type)));

        Element gender = doc.createElement("gender");
        le.appendChild(gender);
        gender.appendChild(doc.createTextNode(String.valueOf(ui.gender)));

        Element statu = doc.createElement("statu");
        le.appendChild(statu);
        statu.appendChild(doc.createTextNode(String.valueOf(ui.statu)));

        Element phone = doc.createElement("phone");
        le.appendChild(phone);
        phone.appendChild(doc.createTextNode(ui.phone));

        Element enroldate = doc.createElement("enroldate");
        le.appendChild(enroldate);
        enroldate.appendChild(doc.createTextNode(ui.enroldate));

        Element remark = doc.createElement("remark");
        le.appendChild(remark);
        remark.appendChild(doc.createTextNode(ui.remark));

        Element cardsn = doc.createElement("cardsn");
        le.appendChild(cardsn);
        cardsn.appendChild(doc.createTextNode(ui.cardsn));

        Element template1 = doc.createElement("template1");
        le.appendChild(template1);
        template1.appendChild(doc.createTextNode(ui.template1));

        Element template2 = doc.createElement("template2");
        le.appendChild(template2);
        template2.appendChild(doc.createTextNode(ui.template2));

        Element photo = doc.createElement("photo");
        le.appendChild(photo);
        photo.appendChild(doc.createTextNode(ui.photo));

        Element matric_no = doc.createElement("matric_no");
        le.appendChild(matric_no);
        matric_no.appendChild(doc.createTextNode(String.valueOf(ui.matric_no)));

        Element first_name = doc.createElement("first_name");
        le.appendChild(first_name);
        first_name.appendChild(doc.createTextNode(String.valueOf(ui.first_name)));

        Element last_name = doc.createElement("last_name");
        le.appendChild(last_name);
        last_name.appendChild(doc.createTextNode(String.valueOf(ui.last_name)));

        Element middle_name = doc.createElement("middle_name");
        le.appendChild(middle_name);
        middle_name.appendChild(doc.createTextNode(String.valueOf(ui.middle_name)));

        Element madien_name = doc.createElement("madien_name");
        le.appendChild(madien_name);
        madien_name.appendChild(doc.createTextNode(String.valueOf(ui.madien_name)));

        Element dob = doc.createElement("dob");
        le.appendChild(dob);
        dob.appendChild(doc.createTextNode(String.valueOf(ui.dob)));

        Element sex = doc.createElement("sex");
        le.appendChild(sex);
        sex.appendChild(doc.createTextNode(String.valueOf(ui.sex)));

        Element marrital_status = doc.createElement("marrital_status");
        le.appendChild(marrital_status);
        marrital_status.appendChild(doc.createTextNode(String.valueOf(ui.marrital_status)));

        Element religion = doc.createElement("religion");
        le.appendChild(religion);
        religion.appendChild(doc.createTextNode(String.valueOf(ui.religion)));

        Element card = doc.createElement("card");
        le.appendChild(card);
        card.appendChild(doc.createTextNode(String.valueOf(ui.card)));

        Element card_serial = doc.createElement("card_serial");
        le.appendChild(card_serial);
        card_serial.appendChild(doc.createTextNode(String.valueOf(ui.card_serial)));

        Element email = doc.createElement("email");
        le.appendChild(email);
        email.appendChild(doc.createTextNode(String.valueOf(ui.email)));

        Element address = doc.createElement("address");
        le.appendChild(address);
        address.appendChild(doc.createTextNode(String.valueOf(ui.address)));

        Element nationality = doc.createElement("nationality");
        le.appendChild(nationality);
        nationality.appendChild(doc.createTextNode(String.valueOf(ui.nationality)));

        Element state = doc.createElement("state");
        le.appendChild(state);
        state.appendChild(doc.createTextNode(String.valueOf(ui.state)));

        Element lga = doc.createElement("lga");
        le.appendChild(lga);
        lga.appendChild(doc.createTextNode(String.valueOf(ui.lga)));

        Element nok_name = doc.createElement("nok_name");
        le.appendChild(nok_name);
        nok_name.appendChild(doc.createTextNode(String.valueOf(ui.nok_name)));

        Element nok_email = doc.createElement("nok_email");
        le.appendChild(nok_email);
        nok_email.appendChild(doc.createTextNode(String.valueOf(ui.nok_email)));

        Element nok_phone = doc.createElement("nok_phone");
        le.appendChild(nok_phone);
        nok_phone.appendChild(doc.createTextNode(String.valueOf(ui.nok_phone)));

        Element nok_address = doc.createElement("nok_address");
        le.appendChild(nok_address);
        nok_address.appendChild(doc.createTextNode(String.valueOf(ui.nok_address)));

        Element home_town = doc.createElement("home_town");
        le.appendChild(home_town);
        home_town.appendChild(doc.createTextNode(String.valueOf(ui.home_town)));

        Element faculty = doc.createElement("faculty");
        le.appendChild(faculty);
        faculty.appendChild(doc.createTextNode(String.valueOf(ui.faculty)));

        Element department = doc.createElement("department");
        le.appendChild(department);
        department.appendChild(doc.createTextNode(String.valueOf(ui.department)));

        Element specialization = doc.createElement("specialization");
        le.appendChild(specialization);
        specialization.appendChild(doc.createTextNode(String.valueOf(ui.specialization)));

        Element level = doc.createElement("level");
        le.appendChild(level);
        level.appendChild(doc.createTextNode(String.valueOf(ui.level)));

        Element session = doc.createElement("session");
        le.appendChild(session);
        session.appendChild(doc.createTextNode(String.valueOf(ui.session)));

        Element isSyncWithBackend = doc.createElement("isSyncWithBackend");
        le.appendChild(isSyncWithBackend);
        isSyncWithBackend.appendChild(doc.createTextNode(String.valueOf(ui.isSyncWithBackend)));
        return doc;
    }

    //XML File Write
    public static void WriteXmlFile(Document doc, String filename, String codetype) {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            OutputStreamWriter outwriter = new OutputStreamWriter(fos);
            try {
                Source source = new DOMSource(doc);
                Result result = new StreamResult(outwriter);
                Transformer xformer = TransformerFactory.newInstance().newTransformer();
                xformer.setOutputProperty(OutputKeys.ENCODING, codetype);    //"gb2312" "utf-8"
                xformer.transform(source, result);
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }
            outwriter.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //XML File Read
    public static Document ReadXmlFile(String filename) {
        FileInputStream fin = null;
        InputStream inStream = null;
        Document doc = null;
        try {
            fin = new FileInputStream(filename);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        inStream = new BufferedInputStream(fin);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            //
            //ByteArrayInputStream is = new ByteArrayInputStream(txt.getBytes());
            doc = builder.parse(inStream);
            //inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

}
