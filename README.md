tabular-data-normaliser
=======================
The BL holds a dataset that consists of a number of different data files, mostly in CSV or XLS formats, along with some oddities such as PDFs.

While these files contain essentially the same data the files are in different layouts. Some include column headings, some do not, some have multiple columns for names, some use a single column (or even both). Without a single representation for this data, its use, management and preservation become significant problems. We cannot, for example, offer a unified search over the data. Futher, each format would require its own preservation plan and several preservation plans for a single dataset seems rather difficult to sustain.

Given this, we have created this tool to normalise the tabular data and create a unified file containing only those columns that the BL needs to keep.

The tool is designed to use column headers where available and make guesses as to the content of columns if unavailable. These guesses use regular expressions defined in a properties file. While the example properties file (and indeed the code) is geared toward a specific dataset it is hoped that this tool can form the basis of any tabular data normalisation, mitagating against preservation risks of data files on the way.

Further information
===================

http://www.openplanetsfoundation.org/blogs/2013-03-01-tabular-data-normalisation-tool
http://wiki.opf-labs.org/display/SP/Normalise+Disparate+Tabular+Data+Sources
