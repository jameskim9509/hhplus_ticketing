import http from 'k6/http';
import { check, fail, sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export let options = {
    scenarios: {
        payment_scenario: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '10s', target: 50},
                { duration: '1s', target: 0}
            ],
            exec: 'payment_scenario'
        }
      }
};

export function payment_scenario() {
    let userId = randomIntBetween(1, 100);
    let uuid = createToken(userId);
    waitForActive(uuid);
    chargePoint(userId, uuid, 100000);

    let availableDate = getConcert(uuid, '2025-05-01', '2025-07-01');
    let availableSeatNumber = getSeat(uuid, availableDate);
    if (availableSeatNumber === -1) return;

    let reservationId = reserveSeat(uuid, availableDate, availableSeatNumber);
    payment(uuid, reservationId);
}

function createToken(userId) {
    let res = http.post(
        `http://host.docker.internal:8080/concerts/tickets/tokens/${userId}`
    );

    if (
        !check(res, {
          'createToken status was 200': (res) => res.status == 200,
        })
      ) {
        fail('createToken Error');
      }

    return res.json('uuid');
}

function waitForActive(uuid) {
    const params = {
        headers: {
            'X-Custom-Header': uuid
        }
    }

    let waitingNumber = -1;
    while (waitingNumber !== 0) {
        let res = http.get(
            'http://host.docker.internal:8080/concerts/tickets/tokens',
            params
        );

        if(!check(res, {
            'getToken status was 200': (r) => r.status === 200
          })) {
            fail("get Token Error");
        }

        waitingNumber = res.json('waitingNumber');
        if (waitingNumber === undefined) {
            fail('get Token Error');
        }
        sleep(1);
    }
}

function chargePoint(userId, uuid, point) {
    let url = `http://host.docker.internal:8080/concerts/balance/${userId}?point=${point}`;
    const params = {
        headers: {
            'X-Custom-Header': uuid
        }
    }

    let res = http.patch(url, "", params);
    if(!check(res, {
        'chargePoint status was 200': (r) => r.status === 200
      })) {
        fail("charge Point Error");
    }
}

function getConcert(uuid, startDate, endDate){
    let url = `http://host.docker.internal:8080/concerts?startDate=${startDate}&endDate=${endDate}`;
    const params = {
        headers: {
            'X-Custom-Header': uuid
        }
    }

    let res = http.get(url, params);
    if(!check(res, {
        'getConcert status was 200': (r) => r.status === 200
      })) {
        fail("get Concerts Error");
    }

    return res.json('availableConcerts.availableConcertDtoList.0.date');
}

function getSeat(uuid, date){
    let url = `http://host.docker.internal:8080/concerts/seats?date=${date}`;
    const params = {
        headers: {
            'X-Custom-Header': uuid
        }
    }

    let res = http.get(url, params);
    if(!check(res, {
        'getSeat status was 200': (r) => r.status === 200
      })) {
        fail("get Seats Error");
    }

    const seats = res.json('availableSeatNumber')
    if(seats.length === 0){
        console.log('available seats is empty')
        return -1;
    }

    const randomIndex = Math.floor(Math.random() * seats.length);
    return seats[randomIndex].seatNumber;
}

function reserveSeat(uuid, date, seatNumber) {
    let url = `http://host.docker.internal:8080/concerts/reservation/seat?date=${date}&seatNumber=${seatNumber}`;
    const params = {
        headers: {
            'X-Custom-Header': uuid
        }
    }

    let res = http.post(url, '', params);
    if(!check(res, {
        'reserveSeat status was 200': (r) => r.status === 200
      })) {
        fail("reserve Seat Error");
    }

    return res.json('id');
}

function payment(uuid, reservationId){
    let url = `http://host.docker.internal:8080/concerts/payment?reservationId=${reservationId}`;
    const params = {
        headers: {
            'X-Custom-Header': uuid
        }
    }

    let res = http.patch(url, '', params);
    if(!check(res, {
        'payment status was 200': (r) => r.status === 200
      })) {
        fail("payment Error");
    }
}