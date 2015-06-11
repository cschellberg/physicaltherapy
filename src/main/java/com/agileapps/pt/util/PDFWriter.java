package com.agileapps.pt.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/*import org.apache.poi.xwpf.usermodel.XWPFDocument;
 import org.apache.poi.xwpf.usermodel.XWPFParagraph;
 import org.apache.poi.xwpf.usermodel.XWPFRun;*/





import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.graphics.pdf.PdfDocument.Page;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.graphics.pdf.PdfDocument.PageInfo.Builder;

import com.agileapps.pt.pojos.FormTemplate;


public class PDFWriter {


	public static PrintDocumentAdapter getPrinterAdapter(Activity activity,
			FormTemplate formTemplate) {
		return new PTPrintDocumentAdapter(activity) ;
	}

	private static class PTPrintDocumentAdapter extends PrintDocumentAdapter {
		private Activity activity;
		private int pageHeight;
		private int pageWidth;
		public PrintedPdfDocument ptPdfDocument;
		public int totalpages = 1;

		private PTPrintDocumentAdapter(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void onLayout(PrintAttributes oldAttributes,
				PrintAttributes newAttributes,
				CancellationSignal cancellationSignal,
				LayoutResultCallback callback, Bundle extras) {
			ptPdfDocument = new PrintedPdfDocument(
					activity.getApplicationContext(), newAttributes);
			pageHeight = newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
			pageWidth = newAttributes.getMediaSize().getWidthMils() / 1000 * 72;
			if (cancellationSignal.isCanceled()) {
				callback.onLayoutCancelled();
				return;
			}
			if (totalpages > 0) {
				PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder(
						"print_output.pdf").setContentType(
						PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).setPageCount(
						totalpages);

				PrintDocumentInfo info = builder.build();
				callback.onLayoutFinished(info, true);
			} else {
				callback.onLayoutFailed("Page count is zero.");
			}
		}

		@Override
		public void onWrite(PageRange[] pageRanges,
				ParcelFileDescriptor destination,
				CancellationSignal cancellationSignal,
				WriteResultCallback callback) {
			for (int i = 0; i < totalpages; i++) {
				if (pageInRange(pageRanges, i)) {
					PageInfo newPage = new PageInfo.Builder(pageWidth,
							pageHeight, i).create();

					PdfDocument.Page page = ptPdfDocument.startPage(newPage);

					if (cancellationSignal.isCanceled()) {
						callback.onWriteCancelled();
						ptPdfDocument.close();
						ptPdfDocument = null;
						return;
					}
					drawPage(page, i);
					ptPdfDocument.finishPage(page);
				}
			}

			try {
				ptPdfDocument.writeTo(new FileOutputStream(destination
						.getFileDescriptor()));
			} catch (IOException e) {
				callback.onWriteFailed(e.toString());
				return;
			} finally {
				ptPdfDocument.close();
				ptPdfDocument = null;
			}

			callback.onWriteFinished(pageRanges);
		}

		private void drawPage(Page page, int pageNumber) {
			Canvas canvas = page.getCanvas();
		    pageNumber++; // Make sure page numbers start at 1
		    
		    int titleBaseLine = 72;
		    int leftMargin = 54;

		    Paint paint = new Paint();
		    paint.setColor(Color.BLACK);
		    paint.setTextSize(40);
		    canvas.drawText(
                     "Test Print Document Page " + pageNumber,
                                                   leftMargin,
                                                   titleBaseLine, 
                                                   paint);

		    paint.setTextSize(14);
		    canvas.drawText("This is some test content to verify that custom document printing works", leftMargin, titleBaseLine + 35, paint);
		}

		private boolean pageInRange(PageRange[] pageRanges, int page) {
			for (int i = 0; i < pageRanges.length; i++) {
				if ((page >= pageRanges[i].getStart())
						&& (page <= pageRanges[i].getEnd()))
					return true;
			}
			return false;
		}

	}

}
