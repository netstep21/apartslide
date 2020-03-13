package com.zslide.network;

import com.zslide.data.model.Address;
import com.zslide.models.Apartment;
import com.zslide.models.Dong;
import com.zslide.models.Sido;
import com.zslide.models.Sigungu;
import com.zslide.models.TempApartment;
import com.zslide.network.service.AddressService;

import java.util.List;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by chulwoo on 16. 9. 28..
 */
public class AddressApi {

    private AddressService addressService;

    protected AddressApi(Retrofit retrofit) {
        addressService = retrofit.create(AddressService.class);
    }

    public Observable<List<Address>> items(String dongName) {
        return addressService.getAddresses(dongName);
    }

    public Observable<List<Apartment>> apartment(int dongId) {
        return addressService.getApartment(dongId, "");
    }

    public Observable<List<Apartment>> apartment(long dongId, String apartName) {
        return addressService.getApartment(dongId, apartName);
    }

    public Observable<List<TempApartment>> tempApartments(Address address) {
        return addressService.getTempApartments(address.getId(), "대");
    }

    public Observable<TempApartment> createTempApartment(Address address, String name) {
        return addressService.createTempApartment(address.getId(), name);
    }

    public Observable<List<Sido>> sidos() {
        return addressService.sidos();
    }

    public Observable<List<Sigungu>> sigungus(Sido sido) {
        return addressService.sigungus(sido.getId());
    }

    public Observable<List<Dong>> dongs(Sigungu sigungu) {
        return addressService.dongs(sigungu.getId());
    }

    public Observable<Dong> dong(double lat, double lng) {
        return addressService.dong(lat, lng);
    }
}
