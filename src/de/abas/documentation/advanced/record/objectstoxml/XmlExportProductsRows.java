package de.abas.documentation.advanced.record.objectstoxml;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.abas.documentation.advanced.common.AbstractAjoAccess;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.FieldSet;
import de.abas.erp.db.FieldValueProvider;
import de.abas.erp.db.Query;
import de.abas.erp.db.schema.part.Product;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.SelectionBuilder;

public class XmlExportProductsRows extends AbstractAjoAccess {

	public static void main(String[] args) {
		new XmlExportProductsRows().runClientProgram(args);
	}

	DbContext dbContext = getDbContext();

	String fileName = "XMLExportProductsRows.xml";
	final FieldSet<FieldValueProvider> fieldSet = FieldSet.of("idno",
			"swd",
			"descrOperLang",
			"salesPrice",
			"DBNo",
			"grpNo");
	String[] rowFields = { "productListElem", "elemDescr", "elemQty", "sparePartTab" };

	@Override
	public int run(String[] args) {
		setFileName(args);
		try (FileOutputStream fos = new FileOutputStream(fileName)) {

			// make tags
			final Element rootElement = new Element("ABASData");
			final Element recordSet = new Element("RecordSet");

			// enter tags in document
			final Document document = new Document(rootElement);
			rootElement.addContent(recordSet);
			recordSet.setAttribute("action", "export");

			final List<Product> products = getProducts("10053", "10060");

			// set database and group as RecordSet attributes
			final String dbNo = Integer.toString(products.get(0).getDBNo().getCode());
			final String grpNo = Integer.toString(products.get(0).getGrpNo());
			recordSet.setAttribute("database", dbNo);
			recordSet.setAttribute("group", grpNo);

			for (final Product product : products) {
				final Element record = new Element("Record");
				recordSet.addContent(record);
				makeHeadTag(product, record);
				makeRowTags(product, record);
			}

			// create file
			final XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
			xmlOutputter.output(document, fos);
		} catch (final IOException e) {
			dbContext.out().println(e.getMessage());
			return 1;
		}
		return 0;
	}

	private List<Product> getProducts(String idnoBegin, String idnoEnd) {
		final Query<Product> query = dbContext.createQuery(SelectionBuilder.create(Product.class)
				.add(Conditions.between(Product.META.idno, idnoBegin, idnoEnd)).build());
		query.setFields(fieldSet);
		return query.execute();
	}

	private void makeFieldTags(String[] fields, final Element parent, final FieldValueProvider headOrRow) {
		for (final String fieldName : fields) {
			// set other fields as field tags
			if (!(fieldName.equals("DBNo") || fieldName.equals("grpNo"))) {
				final Element field = new Element("Field");
				field.setAttribute("name", fieldName);
				field.setAttribute("abasType",
						(headOrRow instanceof Product ? Product.META.getField(fieldName).getErpType()
								: Product.Row.META.getField(fieldName).getErpType()));
				field.setText(headOrRow.getString(fieldName));
				parent.addContent(field);
			}
		}
	}

	private void makeHeadTag(final Product product, final Element record) {
		final Element head = new Element("Head");
		record.addContent(head);
		makeFieldTags(fieldSet.getFields(), head, product);
	}

	private void makeRowTags(final Product product, final Element record) {
		for (final Product.Row productRow : product.table().getRows()) {
			final Element row = new Element("Row");
			record.addContent(row);
			row.setAttribute("number", Integer.toString(productRow.getRowNo()));
			makeFieldTags(rowFields, row, productRow);
		}
	}

	private void setFileName(String[] args) {
		if (args.length >= 2) {
			fileName = args[1];
		}
	}

}
