import { useState } from 'react';
import OtpInput from "./otp-input.js"

const PhoneOtpForm = ()=>{
    const [phoneNumber,setPhoneNumber]=useState("");
    const [showOtpInput,setShowOtpInput]=useState(false);
    
    const handlePhoneNumber = (event) =>{
            setPhoneNumber(event.target.value);
    }
      const handlePhoneSubmit = (event) =>{
            event.preventDefault();
              //phone validations
              const regex = /[^0-9]/g;
              if(phoneNumber.length <10 || regex.test(phoneNumber)){
                  alert("Invalid Phone Number");
                  return;
              }
          //call BE API
          //show Otp field
          setShowOtpInput(true);
    }
const onOtpSubmit = ()=>{
    
}
    
    return (
        <div>
            {!showOtpInput ? <form onSubmit = { handlePhoneSubmit}>
                    <input 
                        type ="text"
                        value = { phoneNumber }
                        onChange = {handlePhoneNumber}
                        placeholder = "Enter Phone Number"/>
                <button type = "submit">Submit</button>
            </form>
                :<div>
                <p>Enter Otp sent to {phoneNumber}</p>
                <OtpInput length = {4} onOtpSubmit ={onOtpSubmit} />
                </div>
            }
        </div>
    );
};
export default PhoneOtpForm;