# Cool service docs

Using the @proto-ref[Service] { service = io.service.Service } service we can do blah blah blah.
It uses the @proto-ref[CoolMessage] { message = io.messages.CoolMessage } message to send input data, 
and @proto-ref[AnotherCoolMessage] { message = io.message.AnotherCoolMessage } message to send results.
Result code is stored in @proto-ref[CoolEnum] { enum = io.enums.CoolEnum }. 
@proto-ref[Success] { enum = io.enums.CoolEnum, value = Success } indicates successful return,
otherwise the value will be @proto-ref[Success] { enum = io.enums.CoolEnum, value = Failure }.


@@proto-def { service=io.service.Service } 
