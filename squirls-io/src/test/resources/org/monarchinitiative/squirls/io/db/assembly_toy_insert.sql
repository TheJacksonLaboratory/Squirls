-- contigs have toy lengths for easier calculations
insert into SQUIRLS.GENOMIC_ASSEMBLY(ID, NAME, ORGANISM_NAME, TAXON_ID, SUBMITTER, DATE, GENBANK_ACCESSION,
                                     REFSEQ_ACCESSION)
values (1, 'GRCh37.p13', 'Homo sapiens (human)', '9606', 'Genome Reference Consortium', '2013-06-28',
        'GCA_000001405.14', 'GCF_000001405.25');
insert into SQUIRLS.CONTIGS(ASSEMBLY_ID, CONTIG_ID, NAME, SEQUENCE_ROLE, LENGTH, GENBANK_ACCESSION, REFSEQ_ACCESSION,
                            UCSC_NAME)
values (1, 1, '1', 'ASSEMBLED_MOLECULE', 10000, 'CM000663.1', 'NC_000001.10', 'chr1'),
       (1, 2, '2', 'ASSEMBLED_MOLECULE', 20000, 'CM000664.1', 'NC_000002.11', 'chr2'),
       (1, 3, '3', 'ASSEMBLED_MOLECULE', 30000, 'CM000665.1', 'NC_000003.11', 'chr3');