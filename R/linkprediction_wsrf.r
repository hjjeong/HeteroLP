comment= "weight true wsrf"
date = "161004"
option = " undersampling10_clusterpathlen3_levelcolleague_";
typearr= c("TG_DI")# c("hasDisease", "causeDisease", "hasSubstructure","hasGeneFamily","causeSideEffect", "expressIn","hasPathway", "hasChemicalOntology", "express", "proteinProteinInteraction");

total_tp=0;
total_tn=0;
total_fp=0;
total_fn=0;

hn = TRUE
colleague = TRUE
wei = TRUE
wsrf_on = TRUE

test_opt = "path count_";

if(hn){
  test_opt = paste(test_opt,"hn_",sep="")
}
if(colleague){
  test_opt = paste(test_opt,"colleague",sep="")
}
if(wei){
  test_opt = paste(test_opt,"wsrf_",sep="")
}

file_dir = "D:/workspace/LinkPredictionRefactoring_data/new_result/korea_level/";

fileConn <- file(paste(file_dir,  "result.txt", sep=""), open="at")

writeLines(paste("====================typecorr:", type_corr, "common neighbor:", common_neighbor, "====================","ncn :", ncn, "====================", "wsrf_on:", wsrf_on, sep="\t"), con=fileConn)
writeLines(option, fileConn);
writeLines(comment, fileConn);
for (k in (1:length(typearr)))
{
  
  total_tp=0;
  total_tn=0;
  total_fp=0;
  total_fn=0;
  
  type = typearr[k]
  file_path = paste(file_dir, date," ", type,option, sep="");
  
  for (i in (0:9))
  {
    
    file_name_train = paste(file_path, "train_", i, ".csv", sep=""); 
    
    # table-format data from a file named file_name_train 
    table_train = read.csv(file=file_name_train, header = FALSE)

    if (hn && colleague)
    {
      idx = (3:(length(table_train[1,])-1))
    }
    else if(hn && !colleague){
      idx = (3:(length(table_train[1,])-7))
    }
    else if (!hn && !colleague){
      idx = (3:(length(table_train[1,])-9))
    }
    train=table_train[,idx]

    # to make a model for classification, add label value as factor
    train[,length(train[1,])+1] = as.factor(table_train[,length(table_train[1,])])
    colnames(train)[length(train[1,])] <- "label"
    
    file_name_test = paste(file_path, "test_", i, ".csv", sep="");
    table_test = read.csv(file=file_name_test, header=FALSE)
    
    test=table_test[,idx]
    test_label = as.factor(table_test[,length(table_test[1,])])
    
    if(wsrf_on)
    {
      model.wsrf <- wsrf(label~., data = train, weights = wei, importance = TRUE, ntrees = 100)
      result <- predict.wsrf(model.wsrf, newdata = test, type=c("response"))#prob, response
      
      # of TP
      tp_row = result==1 & test_label==1
      tp = length(test[tp_row,1])
      total_tp  = total_tp + tp;
      # of TN
      tn_row=result==0 & test_label==0
      tn = length(test[tn_row,1])
      total_tn = total_tn + tn;
      # of FP
      fp_row=result==1 &test_label==0
      fp = length(test[fp_row,1])
      total_fp = total_fp + fp;
      # of FN
      fn_row=result==0 & test_label==1
      fn = length(test[fn_row,1])
      total_fn = total_fn + fn;
    }else
    {
      model <- randomForest(label~., data=train, importance=TRUE, proximity=FALSE, ntree=100)
      result <- predict(model, newdata = test, predict.all=TRUE)
      
      # of TP
      tp_row = result$aggregate==1 & test_label==1
      tp = length(test[tp_row,1])
      total_tp  = total_tp + tp;
      # of TN
      tn_row=result$aggregate==0 & test_label==0
      tn = length(test[tn_row,1])
      total_tn = total_tn + tn;
      # of FP
      fp_row=result$aggregate==1 &test_label==0
      fp = length(test[fp_row,1])
      total_fp = total_fp + fp;
      # of FN
      fn_row=result$aggregate==0 & test_label==1
      fn = length(test[fn_row,1])
      total_fn = total_fn + fn;
    }
    
    #accuracy 
    accuracy= (tp+tn)/(tp+tn+fp+fn)
    #precision
    precision = tp/(tp+fp)
    #recall
    recall = tp/(tp+fn)
    
    print(paste(accuracy, precision, recall,  paste(tp,"/",fp, sep=""), paste(tp,"/",tn, sep=""), sep=","));
    
  }

  #accuracy 
  accuracy= (total_tp+total_tn)/(total_tp+total_tn+total_fp+total_fn)
  #precision
  precision = total_tp/(total_tp+total_fp)
  #recall
  recall = total_tp/(total_tp+total_fn)
  fscore=2*(precision*recall)/(precision+recall)
  
  result = paste(accuracy, fscore, precision, recall,  paste(total_tp,"/",total_tp+total_fp, sep=""), paste(total_tp,"/", total_tp+total_fn, sep=""), sep=",")
 
  print(result);
}
writeLines("========================================", con=fileConn)
close(fileConn)

