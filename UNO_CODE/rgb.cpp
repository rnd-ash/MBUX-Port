#include "rgb.h"

RGB_Channel::RGB_Channel(uint8_t idx) {
    this->idx = idx;
    this->steps_left = 0;
    this->d_b = 0;
    this->d_g = 0;
    this->d_r = 0;

    this->c_b = 0;
    this->c_g = 0;
    this->c_r = 0;
}

int RGB_Channel::update(Adafruit_TLC5947* tlc) {
    if (this->steps_left > 0) {
        this->c_r += this->d_r;
        this->c_g += this->d_g;
        this->c_b += this->d_b;
        tlc->setLED(this->idx, (uint16_t)this->c_r, (uint16_t)this->c_g, (uint16_t)this->c_b);
        this->steps_left--;
        return 1;
    } else {
        return 0;
    }
}

void RGB_Channel::set_colour(uint8_t step_count, uint8_t r, uint8_t g, uint8_t b, bool save_to_eeprom) {
    if (step_count == 0) {
        this->c_r = r << 4;
        this->c_g = g << 4;
        this->c_b = b << 4;
        this->steps_left = 1;
        this->d_r = 0;
        this->d_g = 0;
        this->d_b = 0;
    } else {
        this->steps_left = step_count;
        this->d_r = ((float)(r << 4) - this->c_r) / (float)step_count;
        this->d_g = ((float)(g << 4) - this->c_g) / (float)step_count;
        this->d_b = ((float)(b << 4) - this->c_b) / (float)step_count;
    }
}




RGB_Manager::RGB_Manager() {
    this->tlc = new Adafruit_TLC5947(1, TLC5947_CLOCK, TLC5974_DATA, TLC5947_LATCH);
    this->tlc->begin();
    // Set all the channels to default colour
    // TODO set default colour for zones in EEPROM
    for (uint8_t i = 0; i < 8; i++) {
        this->channels[i] = new RGB_Channel(i);
        this->channels[i]->set_colour(0, DEFAULT_R, DEFAULT_G, DEFAULT_B, false);
    }
}


void RGB_Manager::write_channel(uint8_t channel_id, uint8_t step_count, uint8_t r, uint8_t g, uint8_t b, bool save_to_eeprom) {
    if (channel_id < 8) { // Sanity check
        this->channels[channel_id]->set_colour(step_count, r, g, b, save_to_eeprom);
    }
}

void RGB_Manager::update() {
    int res = 0;
    for (uint8_t i = 0; i < 8; i++) {
        res |= this->channels[i]->update(this->tlc);
    }
    if (res) { // Only write if channel needs to update
        this->tlc->write(); // Only write to TLC after we update all the channels
    }
}