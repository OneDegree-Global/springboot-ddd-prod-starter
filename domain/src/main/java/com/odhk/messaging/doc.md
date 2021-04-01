# Messaging Interface

### IMessageCallback
通訊介面consumer統一使用的callback介面,\
藉由實作介面定義的callback並傳入consumer來完成callback註冊

###IMeesageQueueProxy
所有MessageProxy實體需繼承的抽象類別, 定義所有proxy共通的操作\
例如與MQ server的session建立與管理\
創建MQ,刪除MQ等

=========================================

### IMessageConsumer
最基本的consumer形式,傳入callback,\
consumer的worker thread會持續監聽事件直到consumer被cancel為止\
監聽單一特定的Queue\
Non-blocking的形式\

### IMessageReceiver
以Synchroonouse的形式接受message （不適用callback) \
包含blocking和non-blocking的形式


### IMessageProducer
最基本的Producer形式, 針對單一特定的Queue發送訊息\
Non-blocking的形式

==========================================


### IMessageSubscriber
行為同IMessageConumser，但是監聽一個topic(event),\
而非特定的Queue\
Non-blocking的形式

### IMessagePublisher
發出Topic (Event) 到Event bus, 所有監聽特定Topic的
Subscriber都會收到\
Non-blocking的形式

==========================================

### IMessageCaller
以RPC的形式處理Message, send Message之後會等到接收端收到訊息並回傳結果\
Blocking的形式, 需支援Timeout

### IMessageCallee
以RPC的形式處理Message, consume message之後需向Producer回傳結果

==========================================

